package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.model.Role;
import ru.sbercourse.filmlibrary.repository.RoleRepository;

@RestController
@RequestMapping("/role")
@Tag(name = "Роли", description = "Контроллер для работы с ролями пользователей фильмотеки")
public class RoleController extends GenericController<Role>{

    public RoleController(RoleRepository roleRepository) {
        setRepository(roleRepository);
    }

}
