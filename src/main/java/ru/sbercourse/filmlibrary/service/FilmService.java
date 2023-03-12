package ru.sbercourse.filmlibrary.service;

import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.repository.FilmRepository;

@Service
public class FilmService extends GenericService<Film>{

    protected FilmService(FilmRepository filmRepository) {
        super(filmRepository);
    }
}
