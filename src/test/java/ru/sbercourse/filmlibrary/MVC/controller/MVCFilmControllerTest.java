package ru.sbercourse.filmlibrary.MVC.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.sbercourse.filmlibrary.dto.AddFilmDirectorDto;
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.dto.FilmSearchDTO;
import ru.sbercourse.filmlibrary.mapper.FilmMapper;
import ru.sbercourse.filmlibrary.model.Genre;
import ru.sbercourse.filmlibrary.service.FilmService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MVCFilmControllerTest extends CommonTestMVC {

    private final FilmDto testFilmDto =
            new FilmDto("MVC_Test Title", (short) 0, "MVC_Test Country",
                    Genre.ACTION, new HashSet<>(), new HashSet<>());
    private final FilmSearchDTO testFilmSearchDTO = new FilmSearchDTO(testFilmDto.getTitle(), "", null);
    private Long testFilmId;
    private final Long testDirectorId = 1L;
    @Autowired
    FilmService filmService;
    @Autowired
    FilmMapper filmMapper;



    @Test
    protected void getAll() throws Exception {
        mvc.perform(get("/films")
                        .param("page", "1")
                        .param("size", "5")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/viewAllFilms"))
                .andExpect(model().attributeExists("films"));
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void create() throws Exception {
        mvc.perform(get("/films/add")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/addFilm"));

        mvc.perform(post("/films/add")
                        .flashAttr("filmForm", testFilmDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films"))
                .andExpect(redirectedUrlTemplate("/films"));
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void update() throws Exception {
        FilmDto foundFilmForUpdate = filmMapper.toDto(filmService.getByTitle(testFilmDto.getTitle()).get(0));
        testFilmId = foundFilmForUpdate.getId();
        foundFilmForUpdate.setCountry("MVC_Test Country_UPDATED");

        mvc.perform(get("/films/update/" + testFilmId)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/updateFilm"))
                .andExpect(model().attributeExists("film"));

        mvc.perform(post("/films/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("filmForm", foundFilmForUpdate)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films"))
                .andExpect(redirectedUrl("/films"));

        foundFilmForUpdate = filmMapper.toDto(filmService.getOne(testFilmId));
        assertEquals("MVC_Test Country_UPDATED", foundFilmForUpdate.getCountry());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void softDelete() throws Exception {
        FilmDto foundFilmForSoftDelete = filmMapper.toDto(filmService.getOne(testFilmId));
        assertFalse(foundFilmForSoftDelete.isDeleted());

        mvc.perform(get("/films/delete/" + testFilmId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films"))
                .andExpect(redirectedUrl("/films"));

        foundFilmForSoftDelete = filmMapper.toDto(filmService.getOne(testFilmId));
        assertTrue(foundFilmForSoftDelete.isDeleted());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void restore() throws Exception {
        FilmDto foundFilmForRestore = filmMapper.toDto(filmService.getOne(testFilmId));
        assertTrue(foundFilmForRestore.isDeleted());

        mvc.perform(get("/films/restore/" + testFilmId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films"))
                .andExpect(redirectedUrl("/films"));

        foundFilmForRestore = filmMapper.toDto(filmService.getOne(testFilmId));
        assertFalse(foundFilmForRestore.isDeleted());
    }

    @Test
    @Order(5)
    protected void viewOneFilm() throws Exception {
        mvc.perform(get("/films/" + testFilmId)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/viewFilm"))
                .andExpect(model().attributeExists("film"));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    @Rollback
    void addDirector() throws Exception {
        mvc.perform(get("/films/addDirector/" + testFilmId)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/addDirector"))
                .andExpect(model().attributeExists("directors"))
                .andExpect(model().attributeExists("filmId"))
                .andExpect(model().attributeExists("filmTitle"));

        FilmDto existingFilm = filmMapper.toDto(filmService.getOne(testFilmId));
        assertFalse(existingFilm.getDirectorsIds().contains(testDirectorId));

        AddFilmDirectorDto addFilmDirectorDto = new AddFilmDirectorDto(testFilmId, testDirectorId);

        mvc.perform(post("/films/addDirector")
                        .flashAttr("filmsDirectorForm", addFilmDirectorDto)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/films/" + addFilmDirectorDto.getFilmId()))
                .andExpect(redirectedUrl("/films/" + addFilmDirectorDto.getFilmId()));

        existingFilm = filmMapper.toDto(filmService.getOne(testFilmId));
        assertTrue(existingFilm.getDirectorsIds().contains(testDirectorId));
    }

    @Test
    @Order(7)
    void searchFilms() throws Exception {
        mvc.perform(post("/films/search")
                        .param("page", "1")
                        .param("size", "5")
                        .flashAttr("filmSearchForm", testFilmSearchDTO)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("films/viewAllFilms"))
                .andExpect(model().attributeExists("films"));

        filmService.delete(testFilmId);
    }
}
