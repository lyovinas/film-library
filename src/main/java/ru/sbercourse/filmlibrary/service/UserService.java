package ru.sbercourse.filmlibrary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.constants.MailConstants;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.UserRepository;
import ru.sbercourse.filmlibrary.utils.MailUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.MANAGER;
import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.USER;

@Service
public class UserService extends GenericService<User> {

  private final RoleService roleService;
  private final UserRepository repository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JavaMailSender javaMailSender;
  @Value("${spring.mail.username}")
  private String mailServerUsername;

  protected UserService(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender javaMailSender) {
    super(userRepository);
    this.roleService = roleService;
    this.repository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.javaMailSender = javaMailSender;
  }


  public List<Order> getOrders(Long id) {
    return getOne(id).getOrders().stream().toList();
  }

  @Override
  public User create(User object) {
    Role userRole = roleService.getByTitle(USER);
    if (userRole == null) {
      userRole = roleService.create(new Role(USER, "Пользователь"));
    }
    object.setRole(userRole);
    object.setPassword(bCryptPasswordEncoder.encode(object.getPassword()));
    return super.create(object);
  }

  @Override
  public User update(User object) {
    User foundUser = getOne(object.getId());

    object.setCreatedWhen(foundUser.getCreatedWhen());
    object.setCreatedBy(foundUser.getCreatedBy());
    object.setDeleted(foundUser.isDeleted());
    object.setDeletedWhen(foundUser.getDeletedWhen());
    object.setDeletedBy(foundUser.getDeletedBy());
    object.setUpdatedWhen(LocalDateTime.now());
    object.setUpdatedBy(object.getLogin());

    object.setPassword(foundUser.getPassword());
    object.setRole(foundUser.getRole());
    object.setOrders(foundUser.getOrders());

    return super.update(object);
  }

  public Object getUserByLogin(String login) {
    return repository.findUserByLogin(login);
  }

  public User getUserByEmail(String email) {
    return repository.findUserByEmail(email);
  }

  public void createManager(User object) {
    Role userRole = roleService.getByTitle(MANAGER);
    if (userRole == null) {
      userRole = roleService.create(new Role(MANAGER, "Управляющий"));
    }
    object.setRole(userRole);
    object.setPassword(bCryptPasswordEncoder.encode(object.getPassword()));
    super.create(object);
  }

  public boolean checkPassword(String password, UserDetails foundUser) {
    return bCryptPasswordEncoder.matches(password, foundUser.getPassword());
  }

  public void sendChangePasswordEmail(User user) {
    UUID uuid = UUID.randomUUID();
    user.setChangePasswordToken(uuid.toString());
    user.setUpdatedWhen(LocalDateTime.now());
    user.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    repository.save(user);
    SimpleMailMessage message = MailUtils.createEmailMessage(
            mailServerUsername,
            user.getEmail(),
            MailConstants.MAIL_SUBJECT_FOR_REMEMBER_PASSWORD,
            MailConstants.MAIL_MESSAGE_FOR_REMEMBER_PASSWORD +
                    "http://localhost:8080/users/change-password?uuid=" + uuid
    );
    javaMailSender.send(message);
  }

  public void changePassword(String uuid, String password) {
    User user = repository.getByChangePasswordToken(uuid);
    user.setPassword(bCryptPasswordEncoder.encode(password));
    user.setChangePasswordToken(null);
    user.setUpdatedWhen(LocalDateTime.now());
    user.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    repository.save(user);
  }
}
