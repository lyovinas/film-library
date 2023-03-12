package ru.sbercourse.filmlibrary.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sbercourse.filmlibrary.dto.UserDto;
import ru.sbercourse.filmlibrary.model.GenericModel;
import ru.sbercourse.filmlibrary.model.User;
import ru.sbercourse.filmlibrary.repository.OrderRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper
    extends GenericMapper<User, UserDto> {

  private final OrderRepository orderRepository;

  protected UserMapper(ModelMapper modelMapper, OrderRepository orderRepository) {
    super(modelMapper, User.class, UserDto.class);
    this.orderRepository = orderRepository;
  }

  @PostConstruct
  protected void setupMapper() {
    modelMapper.createTypeMap(User.class, UserDto.class)
        .addMappings(m -> m.skip(UserDto::setOrdersIds)).setPostConverter(toDtoConverter());
    modelMapper.createTypeMap(UserDto.class, User.class)
        .addMappings(m -> m.skip(User::setOrders)).setPostConverter(toEntityConverter());
  }

  @Override
  protected void mapSpecificFields(UserDto source, User destination) {
    if (!Objects.isNull(source.getOrdersIds())) {
      destination.setOrders(new HashSet<>(orderRepository.findAllById(source.getOrdersIds())));
    }
    else {
      destination.setOrders(Collections.emptySet());
    }
  }

  @Override
  protected void mapSpecificFields(User source, UserDto destination) {
    destination.setOrdersIds(getOrdersIds(source));
  }

  protected Set<Long> getOrdersIds(User entity) {
    return Objects.isNull(entity) || Objects.isNull(entity.getOrders())
        ? null
        : entity.getOrders().stream()
            .map(GenericModel::getId)
            .collect(Collectors.toSet());
  }
}
