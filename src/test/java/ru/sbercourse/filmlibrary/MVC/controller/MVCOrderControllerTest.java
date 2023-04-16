package ru.sbercourse.filmlibrary.MVC.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.sbercourse.filmlibrary.dto.OrderDto;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.service.OrderService;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
@Transactional
class MVCOrderControllerTest {
    protected MockMvc mvc;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    private OrderService orderService;
    private final Long existingOrderId = 1L;
    private final Long existingFilmId = 1L;
    private final int existingUserId = 1;



    @BeforeEach
    public void prepare() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
    }



    @Test
    void getAllUserOrders() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(existingUserId, null, null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mvc.perform(get("/orders/user")
                        .param("page", "1")
                        .param("size", "5")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("orders/viewAllUserOrders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void softDelete() throws Exception {
        Order existingOrder = orderService.getOne(existingOrderId);
        existingOrder.setDeleted(false);
        assertFalse(existingOrder.isDeleted());

        mvc.perform(get("/orders/delete/" + existingOrderId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:null"))
                .andExpect(redirectedUrl("null"));

        existingOrder = orderService.getOne(existingOrderId);
        assertTrue(existingOrder.isDeleted());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void restore() throws Exception {
        Order existingOrder = orderService.getOne(existingOrderId);
        existingOrder.setDeleted(true);
        assertTrue(existingOrder.isDeleted());

        mvc.perform(get("/orders/restore/" + existingOrderId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders"))
                .andExpect(redirectedUrl("/orders"));

        existingOrder = orderService.getOne(existingOrderId);
        assertFalse(existingOrder.isDeleted());
    }

    @Test
    void rentFilm() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(existingUserId, null, null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mvc.perform(get("/orders/rentFilm/" + existingFilmId)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("orders/rentFilm"))
                .andExpect(model().attributeExists("film"));

        OrderDto orderDto = new OrderDto((long) existingUserId, existingFilmId, null, 0, false);
        mvc.perform(post("/orders/rentFilm")
                        .flashAttr("rentForm", orderDto)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films/" + orderDto.getFilmId()))
                .andExpect(redirectedUrl("/films/" + orderDto.getFilmId()));
    }

    @Test
    void purchaseFilm() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(existingUserId, "user", null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mvc.perform(get("/orders/purchaseFilm/" + existingFilmId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films/" + existingFilmId))
                .andExpect(redirectedUrl("/films/" + existingFilmId));
    }
}
