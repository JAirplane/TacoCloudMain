package com.tacocloud.web.api;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Taco;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.messaging.OrderMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/orders", produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class OrderApiController {

    private final OrderMessagingService msgService;
    private final OrderRepository orderRepository;
    private final TacoRepository tacoRepository;

    @Autowired
    public OrderApiController(OrderRepository orderRepository, TacoRepository tacoRepository,
                              OrderMessagingService msgService) {
        this.orderRepository = orderRepository;
        this.tacoRepository = tacoRepository;
        this.msgService = msgService;
    }

    @GetMapping(path = "/{id}")
    public Mono<ResponseEntity<TacoOrder>> getOrderById(@PathVariable("id") Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<TacoOrder> allOrders() {
        return orderRepository.findAll();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TacoOrder> postTacoOrder(@RequestBody TacoOrder order) {
        msgService.sendOrder(order);
        for(Taco taco: order.getTacos()) {
            tacoRepository.save(taco);
        }
        return orderRepository.save(order);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public Mono<ResponseEntity<TacoOrder>> putTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder tacoOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                       tacoOrder.setUser(order.getUser());
                       tacoOrder.setTacos(order.getTacos());
                       tacoOrder.setId(id);
                       orderRepository.save(tacoOrder);
                       return ResponseEntity.ok(tacoOrder);
                   })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public Mono<ResponseEntity<TacoOrder>> patchTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder patch) {
        return orderRepository.findById(id)
                .map(order -> {
                        if (patch.getDeliveryName() != null) {
                            order.setDeliveryName(patch.getDeliveryName());
                        }
                        if (patch.getDeliveryStreet() != null) {
                            order.setDeliveryStreet(patch.getDeliveryStreet());
                        }
                        if (patch.getDeliveryCity() != null) {
                            order.setDeliveryCity(patch.getDeliveryCity());
                        }
                        if (patch.getDeliveryState() != null) {
                            order.setDeliveryState(patch.getDeliveryState());
                        }
                        if (patch.getDeliveryZip() != null) {
                            order.setDeliveryZip(patch.getDeliveryZip());
                        }
                        if (patch.getCcNumber() != null) {
                            order.setCcNumber(patch.getCcNumber());
                        }
                        if (patch.getCcExpiration() != null) {
                            order.setCcExpiration(patch.getCcExpiration());
                        }
                        if (patch.getCcCVV() != null) {
                            order.setCcCVV(patch.getCcCVV());
                        }
                        orderRepository.save(order);
                        return new ResponseEntity<>(order, HttpStatus.OK);
                })
                .defaultIfEmpty(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long id) {
        orderRepository.deleteById(id);
    }
}
