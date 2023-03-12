package ru.sbercourse.filmlibrary.service;

import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.repository.OrderRepository;

@Service
public class OrderService extends GenericService<Order> {

    protected OrderService(OrderRepository orderRepository) {
        super(orderRepository);
    }

}
