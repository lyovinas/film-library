package ru.sbercourse.filmlibrary.service.userdetails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.MANAGER;
import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.USER;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Value("${spring.security.user.name}")
  private String adminUserName;
  @Value("${spring.security.user.password}")
  private String adminPassword;
  @Value("${spring.security.user.roles}")
  private String adminRole;

  private final UserRepository repository;



  public CustomUserDetailsService(UserRepository repository) {
    this.repository = repository;
  }



  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username.equals(adminUserName)) {
      return new CustomUserDetails(
          null,
          username,
          adminPassword,
          List.of(new SimpleGrantedAuthority("ROLE_" + adminRole))
      );
    } else {
      User user = repository.findUserByLogin(username);
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(
          new SimpleGrantedAuthority(
              user.getRole().getId() == 1L
                  ? "ROLE_" + USER
                  : "ROLE_" + MANAGER
          )
      );
      return new CustomUserDetails(user.getId().intValue(), username, user.getPassword(), authorities);
    }
  }
}
