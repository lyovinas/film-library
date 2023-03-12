package ru.sbercourse.filmlibrary.service;

import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.GenericRepository;

import java.util.List;

@Service
public class UserService extends GenericService<User> {

  private final RoleService roleService;

  protected UserService(GenericRepository<User> repository, RoleService roleService) {
    super(repository);
    this.roleService = roleService;
  }


  public List<Order> getOrders(Long id) {
    return getOne(id).getOrders().stream().toList();
  }

  @Override
  public User create(User object) {
    Role userRole = roleService.getByTitle("USER");
    if (userRole == null) {
      userRole = roleService.create(new Role("USER", "Роль для пользователя фильмотеки"));
    }
    object.setRole(userRole);
    return super.create(object);
  }
}
