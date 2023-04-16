package ru.sbercourse.filmlibrary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.sbercourse.filmlibrary.config.jwt.JWTTokenUtil;
import ru.sbercourse.filmlibrary.dto.LoginDTO;
import ru.sbercourse.filmlibrary.dto.UserDto;
import ru.sbercourse.filmlibrary.service.RoleService;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetailsService;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected ObjectMapper objectMapper;
    private final HttpHeaders headers = new HttpHeaders();
    private Long testId;
    private final String testUsername = "REST_Test_Username";
    private final String testPassword = "REST_Test_Password";
    @Autowired
    private RoleService roleService;

    private String generateToken() {
        return jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername("l"));
    }

    @BeforeAll
    public void prepare() {
        String token = generateToken();
        headers.add("Authorization", "Bearer " + token);
    }


    @Test
    @Order(1)
    void create() throws Exception {
        UserDto newUserDto = new UserDto(testUsername, testPassword,
                "REST_Test firstName", "REST_Test lastName", "REST_Test middleName",
                LocalDate.now(), "REST_Test phone", "REST_Test address", "REST_Test email",
                null, roleService.getByTitle("USER"), new HashSet<>());
        UserDto createdUserDto = objectMapper.readValue(mvc.perform(post("/rest/users/create")
                                .headers(headers)
                                .content(asJsonString(newUserDto))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(testUsername))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                UserDto.class);
        testId = createdUserDto.getId();
    }

    @Test
    @Order(2)
    void update() throws Exception {
        UserDto existingUser = objectMapper.readValue(mvc.perform(get("/rest/users/getById")
                                .headers(headers)
                                .param("id", String.valueOf(testId))
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                UserDto.class);

        existingUser.setFirstName("REST_Test UPDATED");
        mvc.perform(put("/rest/users/update")
                        .headers(headers)
                        .content(asJsonString(existingUser))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("REST_Test UPDATED"));
    }

    @Test
    @Order(3)
    void softDelete() throws Exception {
        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));

        mvc.perform(delete("/rest/users/softDelete/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));
    }

    @Test
    @Order(4)
    void restore() throws Exception {
        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));

        mvc.perform(put("/rest/users/restore/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));
    }

    @Test
    @Order(5)
    void getAll() throws Exception {
        mvc.perform(get("/rest/users")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(6)
    void getById() throws Exception {
        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId));
    }

    @Test
    @Order(7)
    void auth() throws Exception {
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword);
        mvc.perform(post("/rest/users/auth")
                        .headers(headers)
                        .content(asJsonString(loginDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(testUsername));
    }

    @Test
    @Order(8)
    void deleteById() throws Exception {
        mvc.perform(get("/rest/users/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId));

        mvc.perform(delete("/rest/users/deleteById")
                        .headers(headers)
                        .param("id", String.valueOf(testId)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }



    protected String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}