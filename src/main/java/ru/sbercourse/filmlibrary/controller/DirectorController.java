package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;

@RestController
@RequestMapping("/director")
@Tag(name = "Создатели", description = "Контроллер для работы с создателями фильмов")
public class DirectorController extends GenericController<Director> {

    public DirectorController(DirectorRepository directorRepository) {
        setRepository(directorRepository);
    }

}
