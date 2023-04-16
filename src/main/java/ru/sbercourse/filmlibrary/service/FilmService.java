package ru.sbercourse.filmlibrary.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.dto.AddFilmDirectorDto;
import ru.sbercourse.filmlibrary.dto.FilmSearchDTO;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.repository.FilmRepository;

import java.util.List;

@Service
public class FilmService extends GenericService<Film>{

    private final FilmRepository filmRepository;
    private final DirectorService directorService;



    protected FilmService(FilmRepository filmRepository, @Lazy DirectorService directorService) {
        super(filmRepository);
        this.filmRepository = filmRepository;
        this.directorService = directorService;
    }



    @Override
    public Film update(Film newFilm) {
        Film existingFilm = getOne(newFilm.getId());
        if (newFilm.getTitle() != null) existingFilm.setTitle(newFilm.getTitle());
        if (newFilm.getPremierYear() != null) existingFilm.setPremierYear(newFilm.getPremierYear());
        if (newFilm.getCountry() != null) existingFilm.setCountry(newFilm.getCountry());
        if (newFilm.getGenre() != null) existingFilm.setGenre(newFilm.getGenre());
        if (newFilm.getDirectors() != null) existingFilm.setDirectors(newFilm.getDirectors());
        return super.update(existingFilm);
    }

    public Page<Film> findFilms(FilmSearchDTO filmSearchDTO, Pageable pageable) {
        String genre = filmSearchDTO.getGenre() != null ? String.valueOf(filmSearchDTO.getGenre().ordinal()) : "%";
        return filmRepository.searchFilms(genre, filmSearchDTO.getFilmTitle(),
                filmSearchDTO.getDirectorsFio(), pageable);
    }

    public void addDirector(AddFilmDirectorDto addFilmDirectorDto) {
        Film film = getOne(addFilmDirectorDto.getFilmId());
        film.getDirectors().add(directorService.getOne(addFilmDirectorDto.getDirectorId()));
        update(film);
    }

    public List<Film> getByTitle(String title) {
        return filmRepository.getByTitle(title);
    }
}
