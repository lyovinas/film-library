package ru.sbercourse.filmlibrary.MVC.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@WebAppConfiguration
abstract class CommonTestMVC {
  protected MockMvc mvc;
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private FilterChainProxy springSecurityFilterChain;
  @Autowired
  protected WebApplicationContext webApplicationContext;



  @BeforeEach
  public void prepare() {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .alwaysDo(print())
        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        .build();
  }



  protected abstract void getAll() throws Exception;

  protected abstract void create() throws Exception;

  protected abstract void update() throws Exception;

  protected abstract void softDelete() throws Exception;

  protected abstract void restore() throws Exception;
}
