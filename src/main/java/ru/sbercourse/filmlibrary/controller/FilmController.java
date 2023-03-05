package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;
import ru.sbercourse.filmlibrary.repository.FilmRepository;

@RestController
@RequestMapping("/film")
@Tag(name = "Фильмы", description = "Контроллер для работы с фильмами в фильмотеке")
public class FilmController extends GenericController<Film> {
    private FilmRepository filmRepository;
    private DirectorRepository directorRepository;


    public FilmController(FilmRepository filmRepository) {
        setRepository(filmRepository);
    }


    @Operation(summary = "Добавить создателя к фильму",
            description = "Позволяет добавить создателя к фильму по указанным идентификаторам",
            method = "addDirector")
    @PutMapping(value = "/addDirector", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> addDirector(
            @RequestParam(value = "filmId") @Parameter(description = "Идентификатор фильма") Long filmId,
            @RequestParam(value = "directorId") @Parameter(description = "Идентификатор создателя") Long directorId) {
        try {
            Film film = filmRepository.findById(filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с переданным ID не найден"));
            Director director = directorRepository.findById(directorId)
                    .orElseThrow(() -> new NotFoundException("Создатель с переданным ID не найден"));
            film.getDirectors().add(director);
            filmRepository.save(film);
            return ResponseEntity.ok(film);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Autowired
    public void setFilmRepository(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Autowired
    public void setDirectorRepository(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }
}
