package com.tacocloud.web;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.domain.TacoUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {

    private final OrderRepository orderRepository;
    private final PagingProps pagingProps;

    @Autowired
    public OrderController(OrderRepository orderRepository, PagingProps pagingProps) {
        this.orderRepository = orderRepository;
        this.pagingProps = pagingProps;
    }

    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal TacoUser user,
                            @ModelAttribute TacoOrder tacoOrder) {
        if (tacoOrder.getDeliveryName() == null) {
            tacoOrder.setDeliveryName(user.getFullname());
        }
        if (tacoOrder.getDeliveryStreet() == null) {
            tacoOrder.setDeliveryStreet(user.getStreet());
        }
        if (tacoOrder.getDeliveryCity() == null) {
            tacoOrder.setDeliveryCity(user.getCity());
        }
        if (tacoOrder.getDeliveryState() == null) {
            tacoOrder.setDeliveryState(user.getState());
        }
        if (tacoOrder.getDeliveryZip() == null) {
            tacoOrder.setDeliveryZip(user.getZip());
        }
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors,
                               SessionStatus sessionStatus,
                               @AuthenticationPrincipal TacoUser user) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        order.setUser(user);
        orderRepository.save(order);

        sessionStatus.setComplete();
        return "redirect:/";
    }

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal TacoUser user, Model model) {
        Mono<List<TacoOrder>> orders = orderRepository.findAll()
                .sort((order1, order2) -> order1.getPlacedAt().compareTo(order2.getPlacedAt()))
                .take(pagingProps.getOrdersPageSize())
                .collectList();
        model.addAttribute("orders", orders.block());
        return "orderList";
    }
}
