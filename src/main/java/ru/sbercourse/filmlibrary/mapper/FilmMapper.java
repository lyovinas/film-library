package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.model.Film;
import ru.sbercourse.filmlibrary.model.GenericModel;
import ru.sbercourse.filmlibrary.repository.DirectorRepository;
import ru.sbercourse.filmlibrary.repository.OrderRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmMapper extends GenericMapper<Film, FilmDto> {

    private final DirectorRepository directorRepository;
    private final OrderRepository orderRepository;

    protected FilmMapper(ModelMapper modelMapper, DirectorRepository directorRepository,
                         OrderRepository orderRepository) {
        super(modelMapper, Film.class, FilmDto.class);
        this.directorRepository = directorRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    protected void setupMapper() {
        modelMapper.createTypeMap(Film.class, FilmDto.class)
                .addMappings(m -> m.skip(FilmDto::setDirectorsIds)).setPostConverter(toDtoConverter())
                .addMappings(m -> m.skip(FilmDto::setOrdersIds)).setPostConverter(toDtoConverter());
        modelMapper.createTypeMap(FilmDto.class, Film.class)
                .addMappings(m -> m.skip(Film::setDirectors)).setPostConverter(toEntityConverter())
                .addMappings(m -> m.skip(Film::setOrders)).setPostConverter(toEntityConverter());
    }

    @Override
    protected void mapSpecificFields(FilmDto source, Film destination) {
        if (!Objects.isNull(source.getDirectorsIds())) {
            destination.setDirectors(new HashSet<>(directorRepository.findAllById(source.getDirectorsIds())));
        }
        if (!Objects.isNull(source.getOrdersIds())) {
            destination.setOrders(new HashSet<>(orderRepository.findAllById(source.getOrdersIds())));
        }
    }

    @Override
    protected void mapSpecificFields(Film source, FilmDto destination) {
        destination.setDirectorsIds(getDirectorsIds(source));
        destination.setOrdersIds(getOrdersIds(source));
    }

    protected Set<Long> getDirectorsIds(Film entity) {
        return Objects.isNull(entity) || Objects.isNull(entity.getDirectors())
                ? null
                : entity.getDirectors()
                .stream()
                .map(GenericModel::getId)
                .collect(Collectors.toSet());
    }

    protected Set<Long> getOrdersIds(Film entity) {
        return Objects.isNull(entity) || Objects.isNull(entity.getOrders())
                ? null
                : entity.getOrders()
                .stream()
                .map(GenericModel::getId)
                .collect(Collectors.toSet());
    }
}
