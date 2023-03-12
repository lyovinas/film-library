package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.dto.FilmWithDirectorsDto;
import ru.sbercourse.filmlibrary.mapper.FilmMapper;
import ru.sbercourse.filmlibrary.mapper.FilmWithDirectorsMapper;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.service.DirectorService;
import ru.sbercourse.filmlibrary.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/film")
@Tag(name = "Фильмы", description = "Контроллер для работы с фильмами в фильмотеке")
public class FilmController extends GenericController<Film, FilmDto> {
    private final FilmService filmService;
    private final DirectorService directorService;
    private final FilmMapper filmMapper;
    private final FilmWithDirectorsMapper filmWithDirectorsMapper;


    public FilmController(FilmService filmService, FilmMapper filmMapper, DirectorService directorService,
                          FilmWithDirectorsMapper filmWithDirectorsMapper) {
        setService(filmService);
        setMapper(filmMapper);
        this.filmService = filmService;
        this.directorService = directorService;
        this.filmMapper = filmMapper;
        this.filmWithDirectorsMapper = filmWithDirectorsMapper;
    }


    @Operation(summary = "Добавить создателя к фильму",
            description = "Позволяет добавить создателя к фильму по указанным идентификаторам",
            method = "addDirector")
    @PutMapping(value = "/addDirector", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilmDto> addDirector(
            @RequestParam(value = "filmId") @Parameter(description = "Идентификатор фильма") Long filmId,
            @RequestParam(value = "directorId") @Parameter(description = "Идентификатор создателя") Long directorId) {
        try {
            Film film = filmService.getOne(filmId);
            Director director = directorService.getOne(directorId);
            film.getDirectors().add(director);
            return ResponseEntity.ok(filmMapper.toDto(filmService.update(film)));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(description = "Получить список фильмов с создателями")
    @GetMapping(value = "/getFilmsWithDirectors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilmWithDirectorsDto>> getFilmsWithDirectors() {
        return ResponseEntity.ok(filmWithDirectorsMapper.toDtos(filmService.listAll()));
    }
}
