package com.tacocloud.web.api;

import com.tacocloud.domain.TacoOrder;
import com.tacocloud.messaging.OrderMessagingService;
import com.tacocloud.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/orders", produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

    private final OrderMessagingService msgService;
    private final OrderService orderService;

    @Autowired
    public OrderApiController(OrderMessagingService msgService, OrderService orderService) {
        this.msgService = msgService;
        this.orderService = orderService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TacoOrder> getOrderById(@PathVariable("id") Long id) {
        var orderOpt = orderService.findOrderById(id);
        return orderOpt.map(tacoOrder -> new ResponseEntity<>(tacoOrder, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Iterable<TacoOrder> allOrders() {

        return orderService.allOrders();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TacoOrder postTacoOrder(@RequestBody TacoOrder order) {
        msgService.sendOrder(order);
        return orderService.saveOrder(order);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<TacoOrder> putTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder tacoOrder) {
        var savedOrder = orderService.updateOrder(id, tacoOrder);
        return savedOrder.map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<TacoOrder> patchTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder patch) {
        Optional<TacoOrder> oldOrder = orderService.patchOrder(id, patch);
        return oldOrder.map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long id) {

        orderService.deleteOrder(id);
    }
}
