package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sbercourse.filmlibrary.dto.DirectorWithFilmsDto;
import ru.sbercourse.filmlibrary.model.Director;
import ru.sbercourse.filmlibrary.model.GenericModel;
import ru.sbercourse.filmlibrary.repository.FilmRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DirectorWithFilmsMapper extends GenericMapper<Director, DirectorWithFilmsDto> {

  private final FilmRepository filmRepository;

  protected DirectorWithFilmsMapper(ModelMapper modelMapper, FilmRepository filmRepository) {
    super(modelMapper, Director.class, DirectorWithFilmsDto.class);
    this.filmRepository = filmRepository;
  }

  @PostConstruct
  protected void setupMapper() {
    modelMapper.createTypeMap(Director.class, DirectorWithFilmsDto.class)
        .addMappings(m -> m.skip(DirectorWithFilmsDto::setFilmsIds)).setPostConverter(toDtoConverter());
    modelMapper.createTypeMap(DirectorWithFilmsDto.class, Director.class)
        .addMappings(m -> m.skip(Director::setFilms)).setPostConverter(toEntityConverter());
  }

  @Override
  protected void mapSpecificFields(DirectorWithFilmsDto source, Director destination) {
    if(!Objects.isNull(source.getFilmsIds())) {
      destination.setFilms(new HashSet<>(filmRepository.findAllById(source.getFilmsIds())));
    }
  }

  @Override
  protected void mapSpecificFields(Director source, DirectorWithFilmsDto destination) {
    destination.setFilmsIds(getFilmsIds(source));
  }

  protected Set<Long> getFilmsIds(Director entity) {
    return Objects.isNull(entity) || Objects.isNull(entity.getFilms())
        ? null
        : entity.getFilms()
            .stream()
            .map(GenericModel::getId)
            .collect(Collectors.toSet());
  }
}
