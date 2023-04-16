package ru.sbercourse.filmlibrary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.sbercourse.filmlibrary.config.jwt.JWTTokenUtil;
import ru.sbercourse.filmlibrary.dto.DirectorDto;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetailsService;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirectorControllerTest {

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
        DirectorDto newDirectorDto = new DirectorDto("REST_Test directorsFio", "REST_Test position", new HashSet<>());
        DirectorDto createdDirectorDto = objectMapper.readValue(mvc.perform(post("/rest/directors/create")
                                .headers(headers)
                                .content(asJsonString(newDirectorDto))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.directorsFio").value("REST_Test directorsFio"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                DirectorDto.class);
        testId = createdDirectorDto.getId();
    }

    @Test
    @Order(2)
    void update() throws Exception {
        DirectorDto existingDirector = objectMapper.readValue(mvc.perform(get("/rest/directors/getById")
                                .headers(headers)
                                .param("id", String.valueOf(testId))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                DirectorDto.class);

        existingDirector.setDirectorsFio("REST_Test UPDATED");
        mvc.perform(put("/rest/directors/update")
                        .headers(headers)
                        .content(asJsonString(existingDirector))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.directorsFio").value("REST_Test UPDATED"));
    }

    @Test
    @Order(3)
    void softDelete() throws Exception {
        mvc.perform(get("/rest/directors/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));

        mvc.perform(delete("/rest/directors/softDelete/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/directors/getById")
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
        mvc.perform(get("/rest/directors/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));

        mvc.perform(put("/rest/directors/restore/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/directors/getById")
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
    void addFilm() throws Exception {
        mvc.perform(get("/rest/directors/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filmsIds").value(Matchers.not(Matchers.contains(1))));

        mvc.perform(put("/rest/directors/addFilm")
                        .headers(headers)
                        .param("directorId", String.valueOf(testId))
                        .param("filmId", String.valueOf(1L))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filmsIds").value(Matchers.contains(1)));
    }

    @Test
    @Order(6)
    void getDirectorsWithFilms() throws Exception {
        mvc.perform(get("/rest/directors/getDirectorsWithFilms")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(7)
    void getAll() throws Exception {
        mvc.perform(get("/rest/directors")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(8)
    void getById() throws Exception {
        mvc.perform(get("/rest/directors/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId));
    }

    @Test
    @Order(9)
    void deleteById() throws Exception {
        mvc.perform(get("/rest/directors/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId));

        mvc.perform(delete("/rest/directors/deleteById")
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