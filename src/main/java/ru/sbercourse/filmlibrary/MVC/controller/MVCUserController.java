package ru.sbercourse.filmlibrary.MVC.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sbercourse.filmlibrary.dto.UserDto;
import ru.sbercourse.filmlibrary.mapper.UserMapper;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.service.UserService;

import static ru.sbercourse.filmlibrary.constants.UserRolesConstants.ADMIN;

@Controller
@RequestMapping("/users")
public class MVCUserController {

  private final UserService service;
  private final UserMapper mapper;



  public MVCUserController(UserService service, UserMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }



  @GetMapping("/registration")
  public String registration() {
    return "users/registration";
  }

  @PostMapping("/registration")
  public String registration(@ModelAttribute("userForm") UserDto userDto, BindingResult bindingResult) {
    if(userDto.getLogin().equals(ADMIN) || service.getUserByLogin(userDto.getLogin()) != null) {
      bindingResult.rejectValue("login", "error.login", "Такой логин уже существует");
      return "redirect:/users/registration";
    }
    if(service.getUserByEmail(userDto.getEmail()) != null) {
      bindingResult.rejectValue("email", "error.email", "Такая почта уже существует");
      return "redirect:/users/registration";
    }
    service.create(mapper.toEntity(userDto));
    return "redirect:/login";
  }

  @GetMapping("/add-manager")
  public String addManager() {
    return "users/addManager";
  }

  @PostMapping("/add-manager")
  public String addManager(@ModelAttribute("userForm") UserDto userDto, BindingResult bindingResult) {
    if(userDto.getLogin().equals(ADMIN) || service.getUserByLogin(userDto.getLogin()) != null) {
      bindingResult.rejectValue("login", "error.login", "Такой логин уже существует");
      return "redirect:/users/add-manager";
    }
    if(service.getUserByEmail(userDto.getEmail()) != null) {
      bindingResult.rejectValue("email", "error.email", "Такая почта уже существует");
      return "redirect:/users/add-manager";
    }
    service.createManager(mapper.toEntity(userDto));
    return "redirect:/users";
  }

  @GetMapping("")
  public String getAll(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "5") int pageSize,
      Model model
  ) {
    PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Direction.ASC, "login"));
    Page<User> userPage = service.listAll(pageRequest);
    Page<UserDto> userDtoPage = new PageImpl<>(mapper.toDtos(userPage.getContent()), pageRequest, userPage.getTotalElements());
    model.addAttribute("users", userDtoPage);
    return "users/viewAllUsers";
  }

  @GetMapping("/profile/{id}")
  public String viewProfile(@PathVariable Long id, Model model) {
    model.addAttribute("user", mapper.toDto(service.getOne(id)));
    return "profile/viewProfile";
  }

  @GetMapping("/profile/update/{id}")
  public String updateProfile(@PathVariable Long id, Model model) {
    model.addAttribute("user", mapper.toDto(service.getOne(id)));
    return "profile/updateProfile";
  }

  @PostMapping("/profile/update")
  public String updateProfile(@ModelAttribute("userForm") UserDto userDto) {
    service.update(mapper.toEntity(userDto));
    return "redirect:/users/profile/" + userDto.getId();
  }
}
