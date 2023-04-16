package ru.sbercourse.filmlibrary.MVC.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sbercourse.filmlibrary.dto.*;
import ru.sbercourse.filmlibrary.mapper.FilmMapper;
import ru.sbercourse.filmlibrary.mapper.FilmWithDirectorsMapper;
import ru.sbercourse.filmlibrary.mapper.OrderMapper;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.service.DirectorService;
import ru.sbercourse.filmlibrary.service.FilmService;
import ru.sbercourse.filmlibrary.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/films")
public class MVCFilmController {

    private final FilmService filmService;
    private final DirectorService directorService;
    private final FilmMapper filmMapper;
    private final FilmWithDirectorsMapper filmWithDirectorsMapper;
    private final OrderService orderService;
    private final OrderMapper orderMapper;



    public MVCFilmController(FilmService filmService, DirectorService directorService,
                             FilmMapper filmMapper, FilmWithDirectorsMapper filmWithDirectorsMapper, OrderService orderService, OrderMapper orderMapper) {
        this.filmService = filmService;
        this.directorService = directorService;
        this.filmMapper = filmMapper;
        this.filmWithDirectorsMapper = filmWithDirectorsMapper;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }



    @GetMapping("")
    public String getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int pageSize,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "title"));
        Page<Film> filmPage = filmService.listAll(pageRequest);
        Page<FilmWithDirectorsDto> filmWithDirectorsDtoPage =
                new PageImpl<>(filmWithDirectorsMapper.toDtos(filmPage.getContent()), pageRequest, filmPage.getTotalElements());
        model.addAttribute("films", filmWithDirectorsDtoPage);
        return "films/viewAllFilms";
    }

    @GetMapping("/{filmId}")
    public String viewOneFilm(@PathVariable Long filmId, Model model) {
        model.addAttribute("film", filmWithDirectorsMapper.toDto(filmService.getOne(filmId)));
        model.addAttribute("order", orderMapper.toDto(orderService.getUserOrder(filmId)));
        return "films/viewFilm";
    }

    @GetMapping("/add")
    public String addFilm() {
        return "films/addFilm";
    }

    @PostMapping("/add")
    public String addFilm(@ModelAttribute("filmForm") FilmDto filmDto) {
        filmService.create(filmMapper.toEntity(filmDto));
        return "redirect:/films";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        filmService.softDelete(id);
        return "redirect:/films";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        filmService.restore(id);
        return "redirect:/films";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("film", filmMapper.toDto(filmService.getOne(id)));
        return "films/updateFilm";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("filmForm") FilmDto filmDto) {
        filmService.update(filmMapper.toEntity(filmDto));
        return "redirect:/films";
    }

    @PostMapping("/search")
    public String searchFilms(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @ModelAttribute("filmSearchForm") FilmSearchDTO filmSearchDTO,
                              Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "title"));
        Page<Film> filmPage = filmService.findFilms(filmSearchDTO, pageRequest);
        List<FilmWithDirectorsDto> result = filmWithDirectorsMapper.toDtos(filmPage.getContent());
        model.addAttribute("films", new PageImpl<>(result, pageRequest, filmPage.getTotalElements()));
        return "films/viewAllFilms";
    }

    @GetMapping("/addDirector/{filmId}")
    public String addDirector(@PathVariable Long filmId, Model model) {
        model.addAttribute("directors", directorService.listAll());
        model.addAttribute("filmId", filmId);
        model.addAttribute("filmTitle", filmService.getOne(filmId).getTitle());
        return "films/addDirector";
    }

    @PostMapping("/addDirector")
    public String addDirector(@ModelAttribute("filmsDirectorForm") AddFilmDirectorDto addFilmDirectorDto) {
        filmService.addDirector(addFilmDirectorDto);
        return "redirect:/films/" + addFilmDirectorDto.getFilmId();
    }
}
