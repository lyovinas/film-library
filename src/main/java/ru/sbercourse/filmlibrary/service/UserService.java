package ru.sbercourse.filmlibrary.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.MANAGER;
import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.USER;

@Service
public class UserService extends GenericService<User> {

  private final RoleService roleService;
  private final UserRepository repository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  protected UserService(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    super(userRepository);
    this.roleService = roleService;
    this.repository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

  public User createManager(User object) {
    Role userRole = roleService.getByTitle(MANAGER);
    if (userRole == null) {
      userRole = roleService.create(new Role(MANAGER, "Управляющий"));
    }
    object.setRole(userRole);
    object.setPassword(bCryptPasswordEncoder.encode(object.getPassword()));
    return super.create(object);
  }
}
