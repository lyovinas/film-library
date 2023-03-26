package ru.sbercourse.filmlibrary.MVC.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.sbercourse.filmlibrary.dto.AddFilmDirectorDto;
import ru.sbercourse.filmlibrary.dto.DirectorDto;
import ru.sbercourse.filmlibrary.dto.DirectorWithFilmsDto;
import ru.sbercourse.filmlibrary.mapper.DirectorMapper;
import ru.sbercourse.filmlibrary.mapper.DirectorWithFilmsMapper;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.service.DirectorService;
import ru.sbercourse.filmlibrary.service.FilmService;

@Controller
@RequestMapping("/directors")
public class MVCDirectorController {

    private final DirectorMapper directorMapper;
    private final DirectorWithFilmsMapper directorWithFilmsMapper;
    private final DirectorService directorService;
    private final FilmService filmService;



    public MVCDirectorController(DirectorMapper directorMapper, DirectorWithFilmsMapper directorWithFilmsMapper,
                                 DirectorService directorService, FilmService filmService) {
        this.directorMapper = directorMapper;
        this.directorWithFilmsMapper = directorWithFilmsMapper;
        this.directorService = directorService;
        this.filmService = filmService;
    }



    @GetMapping("")
    public String getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int pageSize,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "directorsFio"));
        Page<Director> directorPage = directorService.listAll(pageRequest);
        Page<DirectorWithFilmsDto> directorWithFilmsDtoPage =
                new PageImpl<>(directorWithFilmsMapper.toDtos(directorPage.getContent()), pageRequest, directorPage.getTotalElements());
        model.addAttribute("directors", directorWithFilmsDtoPage);
        return "directors/viewAllDirectors";
    }

//    @GetMapping("/{id}")
//    public String viewDirector(@PathVariable Long id, Model model) {
//        model.addAttribute("director", directorMapper.toDto(directorService.getOne(id)));
//        return "/directors/viewDirector";
//    }

    @GetMapping("/add")
    public String create() {
        return "directors/addDirector";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("directorForm") DirectorDto directorDto) {
        directorService.create(directorMapper.toEntity(directorDto));
        return "redirect:/directors";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        directorService.softDelete(id);
        return "redirect:/directors";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        directorService.restore(id);
        return "redirect:/directors";
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id) {
        model.addAttribute("director", directorService.getOne(id));
        return "directors/updateDirector";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("directorForm") DirectorDto directorDto) {
        directorService.update(directorMapper.toEntity(directorDto));
        return "redirect:/directors";
    }

    @PostMapping("/search")
    public String search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int pageSize,
            @ModelAttribute("directorSearchForm") DirectorDto directorDto,
            Model model
    ) {
        if (!StringUtils.hasText(directorDto.getDirectorsFio()) || !StringUtils.hasLength(directorDto.getDirectorsFio())) {
            return "redirect:/directors";
        } else {
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "directorsFio"));
            Page<Director> directorPage = directorService.searchDirectors(directorDto.getDirectorsFio().trim(), pageRequest);
            Page<DirectorWithFilmsDto> directorWithFilmsDtoPage =
                    new PageImpl<>(directorWithFilmsMapper.toDtos(directorPage.getContent()), pageRequest, directorPage.getTotalElements());
            model.addAttribute("directors", directorWithFilmsDtoPage);
            return "directors/viewAllDirectors";
        }
    }

    @GetMapping("/addFilm/{directorId}")
    public String addFilm(@PathVariable Long directorId, Model model) {
        model.addAttribute("films", filmService.listAll());
        model.addAttribute("directorId", directorId);
        model.addAttribute("directorsFio", directorService.getOne(directorId).getDirectorsFio());
        return "directors/addFilm";
    }

    @PostMapping("/addFilm")
    public String addFilm(@ModelAttribute("directorsFilmForm") AddFilmDirectorDto addFilmDirectorDto) {
        directorService.addFilm(addFilmDirectorDto);
        return "redirect:/directors";
    }
}
