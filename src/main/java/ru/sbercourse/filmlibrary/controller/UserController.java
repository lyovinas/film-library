package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.dto.UserDto;
import ru.sbercourse.filmlibrary.mapper.UserMapper;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.service.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "Пользователи", description = "Контроллер для работы с пользователями фильмотеки")
public class UserController extends GenericController<User, UserDto> {

    public UserController(UserService userService, UserMapper userMapper) {
        setService(userService);
        setMapper(userMapper);
    }
}
