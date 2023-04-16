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
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.model.Genre;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetailsService;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {

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
        FilmDto newFilmDto = new FilmDto("REST_Test title", (short) 2000, "REST_Test country", Genre.ACTION, new HashSet<>(), new HashSet<>());
        FilmDto createdFilmDto = objectMapper.readValue(mvc.perform(post("/rest/films/create")
                                .headers(headers)
                                .content(asJsonString(newFilmDto))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("REST_Test title"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                FilmDto.class);
        testId = createdFilmDto.getId();
    }

    @Test
    @Order(2)
    void update() throws Exception {
        FilmDto existingFilm = objectMapper.readValue(mvc.perform(get("/rest/films/getById")
                                .headers(headers)
                                .param("id", String.valueOf(testId))
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                FilmDto.class);

        existingFilm.setTitle("REST_Test UPDATED");
        mvc.perform(put("/rest/films/update")
                        .headers(headers)
                        .content(asJsonString(existingFilm))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("REST_Test UPDATED"));
    }

    @Test
    @Order(3)
    void softDelete() throws Exception {
        mvc.perform(get("/rest/films/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));

        mvc.perform(delete("/rest/films/softDelete/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/films/getById")
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
        mvc.perform(get("/rest/films/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));

        mvc.perform(put("/rest/films/restore/" + testId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/films/getById")
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
    void addDirector() throws Exception {
        mvc.perform(get("/rest/films/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.directorsIds").value(Matchers.not(Matchers.contains(1))));

        mvc.perform(put("/rest/films/addDirector")
                        .headers(headers)
                        .param("filmId", String.valueOf(testId))
                        .param("directorId", String.valueOf(1L))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.directorsIds").value(Matchers.contains(1)));
    }

    @Test
    @Order(6)
    void getFilmsWithDirectors() throws Exception {
        mvc.perform(get("/rest/films/getFilmsWithDirectors")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(7)
    void getAll() throws Exception {
        mvc.perform(get("/rest/films")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(8)
    void getById() throws Exception {
        mvc.perform(get("/rest/films/getById")
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
        mvc.perform(get("/rest/films/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testId));

        mvc.perform(delete("/rest/films/deleteById")
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