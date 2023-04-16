package ru.sbercourse.filmlibrary.MVC.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.sbercourse.filmlibrary.dto.AddFilmDirectorDto;
import ru.sbercourse.filmlibrary.dto.DirectorDto;
import ru.sbercourse.filmlibrary.mapper.DirectorMapper;
import ru.sbercourse.filmlibrary.service.DirectorService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MVCDirectorControllerTest extends CommonTestMVC {

    private final DirectorDto directorDto = new DirectorDto("MVC_Test DirectorsFio", "MVC_Test Position", new HashSet<>());
    private Long testDirectorId;
    private final Long testFilmId = 1L;
    @Autowired
    private DirectorService directorService;
    @Autowired
    private DirectorMapper directorMapper;

    @Test
    @DisplayName("Получение всех создателей")
    protected void getAll() throws Exception {
        mvc.perform(get("/directors")
                        .param("page", "1")
                        .param("size", "5")
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directors/viewAllDirectors"))
                .andExpect(model().attributeExists("directors"));
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void create() throws Exception {
        mvc.perform(get("/directors/add")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directors/addDirector"));

        mvc.perform(post("/directors/add")
                        .flashAttr("directorForm", directorDto)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrlTemplate("/directors"));
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void update() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "directorsFio"));
        DirectorDto foundDirectorForUpdate = directorMapper.toDto(
                directorService.searchDirectors(directorDto.getDirectorsFio(), pageRequest).getContent().get(0));
        testDirectorId = foundDirectorForUpdate.getId();
        foundDirectorForUpdate.setPosition("MVC_Test UPDATED");

        mvc.perform(get("/directors/update/" + testDirectorId)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directors/updateDirector"))
                .andExpect(model().attributeExists("director"));

        mvc.perform(post("/directors/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("directorForm", foundDirectorForUpdate)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrl("/directors"));

        foundDirectorForUpdate = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertEquals("MVC_Test UPDATED", foundDirectorForUpdate.getPosition());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void softDelete() throws Exception {
        DirectorDto foundDirectorForSoftDelete = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertFalse(foundDirectorForSoftDelete.isDeleted());

        mvc.perform(get("/directors/delete/" + testDirectorId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrl("/directors"));

        foundDirectorForSoftDelete = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertTrue(foundDirectorForSoftDelete.isDeleted());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void restore() throws Exception {
        DirectorDto foundDirectorForRestore = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertTrue(foundDirectorForRestore.isDeleted());

        mvc.perform(get("/directors/restore/" + testDirectorId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrl("/directors"));

        foundDirectorForRestore = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertFalse(foundDirectorForRestore.isDeleted());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    @Rollback
    void addFilm() throws Exception {
        mvc.perform(get("/directors/addFilm/" + testDirectorId)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directors/addFilm"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attributeExists("directorId"))
                .andExpect(model().attributeExists("directorsFio"));

        DirectorDto existingDirector = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertFalse(existingDirector.getFilmsIds().contains(testFilmId));

        AddFilmDirectorDto addFilmDirectorDto = new AddFilmDirectorDto(testFilmId, testDirectorId);

        mvc.perform(post("/directors/addFilm")
                        .flashAttr("directorsFilmForm", addFilmDirectorDto)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrl("/directors"));

        existingDirector = directorMapper.toDto(directorService.getOne(testDirectorId));
        assertTrue(existingDirector.getFilmsIds().contains(testFilmId));
    }

    @Test
    @Order(6)
    void search() throws Exception {
        mvc.perform(post("/directors/search")
                        .param("page", "1")
                        .param("size", "5")
                        .flashAttr("directorSearchForm", new DirectorDto("", "", null))
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/directors"))
                .andExpect(redirectedUrl("/directors"));

        mvc.perform(post("/directors/search")
                        .param("page", "1")
                        .param("size", "5")
                        .flashAttr("directorSearchForm", directorDto)
                        .with(csrf())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("directors/viewAllDirectors"))
                .andExpect(model().attributeExists("directors"));

        directorService.delete(testDirectorId);
    }
}
