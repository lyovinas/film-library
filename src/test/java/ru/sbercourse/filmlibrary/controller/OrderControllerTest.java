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
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.dto.OrderDto;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

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
    private Long testOrderId;
    private final Long testUserId = 1L;
    private final Long testFilmId = 1L;

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
        OrderDto newOrderDto = new OrderDto(testUserId, testFilmId, null, null, false);
        OrderDto createdOrderDto = objectMapper.readValue(mvc.perform(post("/rest/orders/create")
                                .headers(headers)
                                .content(asJsonString(newOrderDto))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(testUserId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                OrderDto.class);
        testOrderId = createdOrderDto.getId();
    }

    @Test
    @Order(2)
    void getRentedAndPurchasedFilmsByUserId() throws Exception {
        mvc.perform(get("/rest/orders/getRentedAndPurchasedFilmsByUserId/" + testUserId)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(3)
    void getRentedFilmsByUserId() throws Exception {
        mvc.perform(get("/rest/orders/getRentedFilmsByUserId/" + testUserId)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(4)
    void update() throws Exception {
        OrderDto existingOrder = objectMapper.readValue(mvc.perform(get("/rest/orders/getById")
                                .headers(headers)
                                .param("id", String.valueOf(testOrderId))
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                OrderDto.class);

        existingOrder.setPurchase(true);
        mvc.perform(put("/rest/orders/update")
                        .headers(headers)
                        .content(asJsonString(existingOrder))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.purchase").value(true));
    }

    @Test
    @Order(5)
    void getPurchasedFilmsByUserId() throws Exception {
        mvc.perform(get("/rest/orders/getPurchasedFilmsByUserId/" + testUserId)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(6)
    void softDelete() throws Exception {
        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));

        mvc.perform(delete("/rest/orders/softDelete/" + testOrderId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));
    }

    @Test
    @Order(7)
    void restore() throws Exception {
        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));

        mvc.perform(put("/rest/orders/restore/" + testOrderId)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(false));
    }

    @Test
    @Order(8)
    void getAll() throws Exception {
        mvc.perform(get("/rest/orders")
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(9)
    void getById() throws Exception {
        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId));
    }

    @Test
    @Order(10)
    void getOrdersByUserId() throws Exception {
        mvc.perform(get("/rest/orders/getOrdersByUserId/" + testUserId)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(11)
    void deleteById() throws Exception {
        mvc.perform(get("/rest/orders/getById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testOrderId));

        mvc.perform(delete("/rest/orders/deleteById")
                        .headers(headers)
                        .param("id", String.valueOf(testOrderId)))
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