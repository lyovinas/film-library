package ru.sbercourse.filmlibrary.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.dto.AddFilmDirectorDto;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;

@Service
public class DirectorService extends GenericService<Director> {

  private final DirectorRepository directorRepository;
  private final FilmService filmService;



  protected DirectorService(DirectorRepository directorRepository, @Lazy FilmService filmService) {
    super(directorRepository);
    this.directorRepository = directorRepository;
    this.filmService = filmService;
  }



  @Override
  public Director update(Director newDirector) {
    Director existingDirector = getOne(newDirector.getId());
    if (newDirector.getDirectorsFio() != null) existingDirector.setDirectorsFio(newDirector.getDirectorsFio());
    if (newDirector.getPosition() != null) existingDirector.setPosition(newDirector.getPosition());
    if (newDirector.getFilms() != null) existingDirector.setFilms(newDirector.getFilms());
    return super.update(existingDirector);
  }

  public Page<Director> searchDirectors(final String fio, Pageable pageable) {
    return directorRepository.getAllByDirectorsFioContainsIgnoreCaseAndIsDeletedFalse(fio, pageable);
  }

  public void addFilm(AddFilmDirectorDto addFilmDirectorDto) {
    Director director = getOne(addFilmDirectorDto.getDirectorId());
    director.getFilms().add(filmService.getOne(addFilmDirectorDto.getFilmId()));
    update(director);
  }
}
