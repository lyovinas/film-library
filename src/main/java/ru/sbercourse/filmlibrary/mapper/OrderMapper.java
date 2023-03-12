package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import ru.sbercourse.filmlibrary.dto.OrderDto;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.repository.FilmRepository;
import ru.sbercourse.filmlibrary.repository.UserRepository;

@Component
public class OrderMapper extends GenericMapper<Order, OrderDto> {

  private final FilmRepository filmRepository;
  private final UserRepository userRepository;

  protected OrderMapper(ModelMapper modelMapper,
                        FilmRepository filmRepository, UserRepository userRepository) {
    super(modelMapper, Order.class, OrderDto.class);
    this.filmRepository = filmRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void setupMapper() {
    super.modelMapper.createTypeMap(Order.class, OrderDto.class)
        .addMappings(m -> m.skip(OrderDto::setUserId)).setPostConverter(toDtoConverter())
        .addMappings(m -> m.skip(OrderDto::setFilmId)).setPostConverter(toDtoConverter());

    super.modelMapper.createTypeMap(OrderDto.class, Order.class)
        .addMappings(m -> m.skip(Order::setUser)).setPostConverter(toEntityConverter())
        .addMappings(m -> m.skip(Order::setFilm)).setPostConverter(toEntityConverter());
  }

  @Override
  protected void mapSpecificFields(OrderDto source, Order destination) {
    destination.setFilm(filmRepository.findById(source.getFilmId())
            .orElseThrow(() -> new NotFoundException("Фильм не найден")));
    destination.setUser(userRepository.findById(source.getUserId())
            .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
  }

  @Override
  protected void mapSpecificFields(Order source, OrderDto destination) {
    destination.setUserId(source.getUser().getId());
    destination.setFilmId(source.getFilm().getId());
  }
}
