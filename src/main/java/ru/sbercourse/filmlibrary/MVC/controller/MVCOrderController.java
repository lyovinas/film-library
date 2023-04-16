package ru.sbercourse.filmlibrary.MVC.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sbercourse.filmlibrary.dto.OrderDto;
import ru.sbercourse.filmlibrary.dto.OrderWithFilmDto;
import ru.sbercourse.filmlibrary.mapper.FilmMapper;
import ru.sbercourse.filmlibrary.mapper.OrderMapper;
import ru.sbercourse.filmlibrary.mapper.OrderWithFilmMapper;
import ru.sbercourse.filmlibrary.model.Order;
import ru.sbercourse.filmlibrary.service.FilmService;
import ru.sbercourse.filmlibrary.service.OrderService;
import ru.sbercourse.filmlibrary.service.userdetails.CustomUserDetails;

@Controller
@RequestMapping("/orders")
public class MVCOrderController {

    private final OrderMapper orderMapper;
    private final OrderWithFilmMapper orderWithFilmMapper;
    private final OrderService orderService;
    private final FilmMapper filmMapper;
    private final FilmService filmService;



    public MVCOrderController(OrderMapper orderMapper, OrderWithFilmMapper orderWithFilmMapper,
                              OrderService orderService, FilmMapper filmMapper, FilmService filmService) {
        this.orderMapper = orderMapper;
        this.orderWithFilmMapper = orderWithFilmMapper;
        this.orderService = orderService;
        this.filmMapper = filmMapper;
        this.filmService = filmService;
    }



//    @GetMapping("")
//    public String getAll(
//            @RequestParam(value = "page", defaultValue = "1") int page,
//            @RequestParam(value = "size", defaultValue = "5") int pageSize,
//            Model model
//    ) {
//        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdWhen"));
//        Page<Order> orderPage = orderService.listAll(pageRequest);
//        Page<OrderDto> orderDtoPage =
//                new PageImpl<>(orderMapper.toDtos(orderPage.getContent()), pageRequest, orderPage.getTotalElements());
//        model.addAttribute("orders", orderDtoPage);
//        return "orders/viewAllOrders";
//    }

    @GetMapping("/user")
    public String getAllUserOrders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int pageSize,
            Model model
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdWhen"));
        Page<Order> orderPage = orderService.getAllUserOrders(pageRequest);
        Page<OrderWithFilmDto> orderDtoPage =
                new PageImpl<>(orderWithFilmMapper.toDtos(orderPage.getContent()), pageRequest, orderPage.getTotalElements());
        model.addAttribute("orders", orderDtoPage);
        return "orders/viewAllUserOrders";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id, HttpServletRequest request) {
        orderService.softDelete(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        orderService.restore(id);
        return "redirect:/orders";
    }

    @GetMapping("/rentFilm/{filmId}")
    public String rentFilm(@PathVariable Long filmId, Model model) {
        model.addAttribute("film", filmMapper.toDto(filmService.getOne(filmId)));
        return "orders/rentFilm";
    }

    @PostMapping("/rentFilm")
    public String rentFilm(@ModelAttribute("rentForm") OrderDto orderDto) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderDto.setUserId(Long.valueOf(customUserDetails.getUserId()));
        orderService.rentFilm(orderMapper.toEntity(orderDto));
        return "redirect:/films/" + orderDto.getFilmId();
    }

    @GetMapping("/purchaseFilm/{filmId}")
    public String purchaseFilm(@PathVariable Long filmId) {
        orderService.purchaseFilm(filmId);
        return "redirect:/films/" + filmId;
    }
}
