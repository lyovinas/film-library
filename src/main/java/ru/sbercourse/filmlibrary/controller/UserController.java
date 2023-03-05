package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.UserRepository;

@RestController
@RequestMapping("/user")
@Tag(name = "Пользователи", description = "Контроллер для работы с пользователями фильмотеки")
public class UserController extends GenericController<User> {

    public UserController(UserRepository userRepository) {
        setRepository(userRepository);
    }

}
