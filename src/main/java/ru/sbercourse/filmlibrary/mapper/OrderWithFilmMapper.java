package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import ru.sbercourse.filmlibrary.dto.OrderWithFilmDto;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.repository.UserRepository;

@Component
public class OrderWithFilmMapper extends GenericMapper<Order, OrderWithFilmDto> {

  private final UserRepository userRepository;

  protected OrderWithFilmMapper(ModelMapper modelMapper, UserRepository userRepository) {
    super(modelMapper, Order.class, OrderWithFilmDto.class);
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void setupMapper() {
    super.modelMapper.createTypeMap(Order.class, OrderWithFilmDto.class)
        .addMappings(m -> m.skip(OrderWithFilmDto::setUserId)).setPostConverter(toDtoConverter());

    super.modelMapper.createTypeMap(OrderWithFilmDto.class, Order.class)
        .addMappings(m -> m.skip(Order::setUser)).setPostConverter(toEntityConverter());
  }

  @Override
  protected void mapSpecificFields(OrderWithFilmDto source, Order destination) {
    destination.setUser(userRepository.findById(source.getUserId())
            .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
  }

  @Override
  protected void mapSpecificFields(Order source, OrderWithFilmDto destination) {
    destination.setUserId(source.getUser().getId());
  }
}
