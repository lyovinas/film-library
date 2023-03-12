package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import ru.sbercourse.filmlibrary.dto.DirectorDto;
import ru.sbercourse.filmlibrary.dto.DirectorWithFilmsDto;
import ru.sbercourse.filmlibrary.mapper.DirectorMapper;
import ru.sbercourse.filmlibrary.mapper.DirectorWithFilmsMapper;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.service.DirectorService;
import ru.sbercourse.filmlibrary.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/director")
@Tag(name = "Создатели", description = "Контроллер для работы с создателями фильмов")
public class DirectorController extends GenericController<Director, DirectorDto> {

    private final DirectorService directorService;
    private final DirectorMapper directorMapper;
    private final FilmService filmService;
    private final DirectorWithFilmsMapper directorWithFilmsMapper;

    public DirectorController(DirectorService directorService, DirectorMapper directorMapper,
                              FilmService filmService, DirectorWithFilmsMapper directorWithFilmsMapper) {
        setService(directorService);
        setMapper(directorMapper);
        this.directorService = directorService;
        this.directorMapper = directorMapper;
        this.filmService = filmService;
        this.directorWithFilmsMapper = directorWithFilmsMapper;
    }


    @Operation(summary = "Добавить фильм к создателю",
            description = "Позволяет добавить фильм к создателю по указанным идентификаторам",
            method = "addFilm")
    @PutMapping(value = "/addFilm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DirectorDto> addFilm(
            @RequestParam(value = "filmId") @Parameter(description = "Идентификатор фильма") Long filmId,
            @RequestParam(value = "directorId") @Parameter(description = "Идентификатор создателя") Long directorId) {
        try {
            Director director = directorService.getOne(directorId);
            Film film = filmService.getOne(filmId);
            director.getFilms().add(film);
            return ResponseEntity.ok(directorMapper.toDto(directorService.update(director)));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(description = "Получить список создателей с фильмами")
    @GetMapping(value = "/getDirectorsWithFilms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DirectorWithFilmsDto>> getDirectorsWithFilms() {
        return ResponseEntity.ok(directorWithFilmsMapper.toDtos(directorService.listAll()));
    }
}
