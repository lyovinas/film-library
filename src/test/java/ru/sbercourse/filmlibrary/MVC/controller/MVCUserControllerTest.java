package ru.sbercourse.filmlibrary.MVC.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.sbercourse.filmlibrary.dto.UserDto;
import ru.sbercourse.filmlibrary.mapper.UserMapper;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.UserRepository;
import ru.sbercourse.filmlibrary.service.UserService;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetails;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
class MVCUserControllerTest {
    protected MockMvc mvc;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Value("${spring.security.user.name}")
    private String adminUserName;
    private final Long existingUserId = 1L;
    private final String existingUserEmail = "string";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;



    @BeforeEach
    public void prepare() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void registration() throws Exception {
        mvc.perform(get("/users/registration")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/registration"));

        UserDto userDto = new UserDto(adminUserName, "", "", "", "",
                LocalDate.now(), "", "", "", null, new Role(), new HashSet<>());
        mvc.perform(post("/users/registration")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/registration"))
                .andExpect(redirectedUrlTemplate("/users/registration"));

        userDto.setLogin("MVC_Test Login");
        userDto.setEmail(existingUserEmail);
        mvc.perform(post("/users/registration")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/registration"))
                .andExpect(redirectedUrlTemplate("/users/registration"));

        userDto.setEmail("MVC_Test Email");
        mvc.perform(post("/users/registration")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(redirectedUrlTemplate("/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void addManager() throws Exception {
        mvc.perform(get("/users/add-manager")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/addManager"));

        UserDto userDto = new UserDto(adminUserName, "", "", "", "",
                LocalDate.now(), "", "", "", null, new Role(), new HashSet<>());
        mvc.perform(post("/users/add-manager")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/add-manager"))
                .andExpect(redirectedUrlTemplate("/users/add-manager"));

        userDto.setLogin("MVC_Test Login");
        userDto.setEmail(existingUserEmail);
        mvc.perform(post("/users/add-manager")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/add-manager"))
                .andExpect(redirectedUrlTemplate("/users/add-manager"));

        userDto.setEmail("MVC_Test Email");
        mvc.perform(post("/users/add-manager")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"))
                .andExpect(redirectedUrlTemplate("/users"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void getAll() throws Exception {
        mvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "5")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/viewAllUsers"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void viewProfile() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(Math.toIntExact(existingUserId), null, null, null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mvc.perform(get("/users/profile/" + existingUserId)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/viewProfile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void updateProfile() throws Exception {
        mvc.perform(get("/users/profile/update/" + existingUserId)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("profile/updateProfile"));

        UserDto userDto = userMapper.toDto(userService.getOne(existingUserId));
        assertNotEquals("MVC_Test FirstName_UPDATED", userDto.getFirstName());
        userDto.setFirstName("MVC_Test FirstName_UPDATED");
        mvc.perform(post("/users/profile/update")
                        .flashAttr("userForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/profile/" + userDto.getId()))
                .andExpect(redirectedUrlTemplate("/users/profile/" + userDto.getId()));

        userDto = userMapper.toDto(userService.getOne(existingUserId));
        assertEquals("MVC_Test FirstName_UPDATED", userDto.getFirstName());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void rememberPassword() throws Exception {
        mvc.perform(get("/users/remember-password")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/rememberPassword"));

//        UserDto userDto = userMapper.toDto(userService.getOne(existingUserId));
//        mvc.perform(post("/users/remember-password")
//                        .flashAttr("changePasswordForm", userDto)
//                        .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/login"))
//                .andExpect(redirectedUrlTemplate("/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    void changePassword() throws Exception {
        mvc.perform(get("/users/change-password")
                        .param("uuid", "MVC_Test uuid")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/changePassword"));

        User user = userService.getOne(existingUserId);
        user.setChangePasswordToken("MVC_Test uuid");
        userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);

        mvc.perform(post("/users/change-password")
                        .param("uuid", "MVC_Test uuid")
                        .flashAttr("changePasswordForm", userDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(redirectedUrlTemplate("/login"));
    }
}
