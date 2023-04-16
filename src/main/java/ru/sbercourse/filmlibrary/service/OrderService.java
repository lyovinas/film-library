package ru.sbercourse.filmlibrary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.repository.OrderRepository;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetails;

import java.time.LocalDate;
import java.util.Set;

@Service
public class OrderService extends GenericService<Order> {

    @Value("${spring.security.user.name}")
    private String adminUserName;

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final FilmService filmService;

    protected OrderService(OrderRepository orderRepository, UserService userService, FilmService filmService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.filmService = filmService;
    }



    public void rentFilm(Order order) {
        order.setRentDate(LocalDate.now());
        order.setPurchase(false);
        create(order);
    }

    public void purchaseFilm(Long filmId) {
        Order order = getUserOrder(filmId);
        if (order == null) {
            CustomUserDetails customUserDetails =
                    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            order = new Order();
            order.setUser(userService.getOne(Long.valueOf(customUserDetails.getUserId())));
            order.setFilm(filmService.getOne(filmId));
            order.setPurchase(true);
            create(order);
        } else {
            order.setRentDate(null);
            order.setRentPeriod(null);
            order.setPurchase(true);
            update(order);
        }
    }

    public Page<Order> getAllUserOrders(PageRequest pageRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderRepository.getAllByUserIdAndIsDeletedFalse(Long.valueOf(customUserDetails.getUserId()), pageRequest);
    }

    public Order getUserOrder(Long filmId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            if (customUserDetails.getUsername().equals(adminUserName)) return null;
            Long userId = Long.valueOf(customUserDetails.getUserId());
            Set<Order> orders = userService.getOne(userId).getOrders();
            for (Order order : orders) {
                if (order.getFilm().getId().equals(filmId)) {
                    return order;
                }
            }
        }
        return null;
    }
}
