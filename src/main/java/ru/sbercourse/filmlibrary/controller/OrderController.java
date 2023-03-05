package ru.sbercourse.filmlibrary.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.repository.OrderRepository;

@RestController
@RequestMapping("/order")
@Tag(name = "Заказы", description = "Контроллер для работы с заказами пользователей фильмотеки")
public class OrderController extends GenericController<Order> {

    public OrderController(OrderRepository orderRepository) {
        setRepository(orderRepository);
    }

}
