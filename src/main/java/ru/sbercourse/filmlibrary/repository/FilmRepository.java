package ru.sbercourse.filmlibrary.repository;

import org.springframework.stereotype.Repository;
import ru.sbercourse.filmlibrary.model.Film;

@Repository
public interface FilmRepository extends GenericRepository<Film> {
}
