package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.dto.FilmDto;
import ru.sbercourse.filmlibrary.dto.OrderDto;
import ru.sbercourse.filmlibrary.mapper.FilmMapper;
import ru.sbercourse.filmlibrary.mapper.OrderMapper;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.service.OrderService;
import ru.sbercourse.filmlibrary.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Tag(name = "Заказы", description = "Контроллер для работы с заказами пользователей фильмотеки")
public class OrderController extends GenericController<Order, OrderDto> {

    private UserService userService;
    private FilmMapper filmMapper;


    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        setService(orderService);
        setMapper(orderMapper);
    }


    @Operation(description = "Получить список заказов пользователя")
    @GetMapping(value = "/getOrdersByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mapper.toDtos(userService.getOrders(userId)));
    }

    @Operation(description = "Получить список всех арендованных/купленных фильмов у пользователя")
    @GetMapping(value = "/getRentedAndPurchasedFilmsByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilmDto>> getRentedAndPurchasedFilmsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(filmMapper.toDtos(
                userService.getOrders(userId)
                        .stream()
                        .map(Order::getFilm)
                        .collect(Collectors.toSet())
                        .stream().toList()));
    }

    @Operation(description = "Получить список арендованных фильмов у пользователя")
    @GetMapping(value = "/getRentedFilmsByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilmDto>> getRentedFilmsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(filmMapper.toDtos(userService.getOrders(userId)
                .stream().filter(o -> !o.isPurchase())
                .map(Order::getFilm)
                .toList()));
    }

    @Operation(description = "Получить список купленных фильмов у пользователя")
    @GetMapping(value = "/getPurchasedFilmsByUserId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilmDto>> getPurchasedFilmsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(filmMapper.toDtos(userService.getOrders(userId)
                .stream().filter(Order::isPurchase)
                .map(Order::getFilm)
                .toList()));
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setFilmMapper(FilmMapper filmMapper) {
        this.filmMapper = filmMapper;
    }
}
