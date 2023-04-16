package ru.sbercourse.filmlibrary.config.jwt;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetailsService;

import java.util.Arrays;

import static ru.sbercourse.filmlibrary.constants.SecurityConstants.RESOURCES_WHITE_LIST;
import static ru.sbercourse.filmlibrary.constants.SecurityConstants.REST.*;
import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.ADMIN;
import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.MANAGER;

//@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JWTSecurityConfig {

  private final JWTTokenFilter jwtTokenFilter;
  private final CustomUserDetailsService customUserDetailsService;



  public JWTSecurityConfig(JWTTokenFilter jwtTokenFilter, CustomUserDetailsService customUserDetailsService) {
    this.jwtTokenFilter = jwtTokenFilter;
    this.customUserDetailsService = customUserDetailsService;
  }



  @Bean
  public HttpFirewall httpFirewall() {
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    return firewall;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(RESOURCES_WHITE_LIST.toArray(String[]::new)).permitAll()

            .requestMatchers(FILMS_WHITE_LIST.toArray(String[]::new)).permitAll()
            .requestMatchers(DIRECTORS_WHITE_LIST.toArray(String[]::new)).permitAll()
            .requestMatchers(ORDERS_WHITE_LIST.toArray(String[]::new)).permitAll()
            .requestMatchers(USERS_WHITE_LIST.toArray(String[]::new)).permitAll()

            .requestMatchers(FILMS_PERMISSION_LIST.toArray(String[]::new)).hasAnyRole(ADMIN, MANAGER)
            .requestMatchers(DIRECTORS_PERMISSION_LIST.toArray(String[]::new)).hasAnyRole(ADMIN, MANAGER)
            .requestMatchers(ORDERS_PERMISSION_LIST.toArray(String[]::new)).hasAnyRole(ADMIN, MANAGER)
            .requestMatchers(USERS_PERMISSION_LIST.toArray(String[]::new)).hasAnyRole(ADMIN, MANAGER)
        )
        .exceptionHandling()
        .authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
        .and()
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .userDetailsService(customUserDetailsService)
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
