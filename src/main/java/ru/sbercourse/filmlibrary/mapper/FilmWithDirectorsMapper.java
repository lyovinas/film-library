package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sbercourse.filmlibrary.dto.FilmWithDirectorsDto;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.model.GenericModel;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmWithDirectorsMapper extends GenericMapper<Film, FilmWithDirectorsDto> {

  private final DirectorRepository directorRepository;

  protected FilmWithDirectorsMapper(ModelMapper modelMapper, DirectorRepository directorRepository) {
    super(modelMapper, Film.class, FilmWithDirectorsDto.class);
    this.directorRepository = directorRepository;
  }

  @PostConstruct
  protected void setupMapper() {
    modelMapper.createTypeMap(Film.class, FilmWithDirectorsDto.class)
        .addMappings(m -> m.skip(FilmWithDirectorsDto::setDirectorsIds)).setPostConverter(toDtoConverter());
    modelMapper.createTypeMap(FilmWithDirectorsDto.class, Film.class)
        .addMappings(m -> m.skip(Film::setDirectors)).setPostConverter(toEntityConverter());
  }

  @Override
  protected void mapSpecificFields(FilmWithDirectorsDto source, Film destination) {
    destination.setDirectors(new HashSet<>(directorRepository.findAllById(source.getDirectorsIds())));
  }

  @Override
  protected void mapSpecificFields(Film source, FilmWithDirectorsDto destination) {
    destination.setDirectorsIds(getDirectorsIds(source));
  }

  protected Set<Long> getDirectorsIds(Film film) {
    return Objects.isNull(film) || Objects.isNull(film.getId())
        ? null
        : film.getDirectors().stream()
            .map(GenericModel::getId)
            .collect(Collectors.toSet());
  }
}
