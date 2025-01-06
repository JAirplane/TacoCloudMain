package com.tacocloud.web.api;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Taco;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.messaging.jms.OrderMessagingService;
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
    public ResponseEntity<TacoOrder> getOrderById(@PathVariable("id") Long id) {
        var orderOpt = orderRepository.findById(id);
        return orderOpt.map(tacoOrder -> new ResponseEntity<>(tacoOrder, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Iterable<TacoOrder> allOrders() {
        return orderRepository.findAll();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TacoOrder postTacoOrder(@RequestBody TacoOrder order) {
        msgService.sendOrder(order);
        for(Taco taco: order.getTacos()) {
            tacoRepository.save(taco);
        }
        return orderRepository.save(order);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<TacoOrder> putTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder tacoOrder) {
        Optional<TacoOrder> oldOrder = orderRepository.findById(id);
        if(oldOrder.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        oldOrder.ifPresent(order -> tacoOrder.setUser(order.getUser()));
        oldOrder.ifPresent(order -> tacoOrder.setTacos(order.getTacos()));
        tacoOrder.setId(id);

        return new ResponseEntity<>(orderRepository.save(tacoOrder), HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<TacoOrder> patchTacoOrder(@PathVariable("id") Long id, @RequestBody TacoOrder patch) {
        Optional<TacoOrder> oldOrder = orderRepository.findById(id);
        if(oldOrder.isPresent()) {
            var old = oldOrder.get();
            if (patch.getDeliveryName() != null) {
                old.setDeliveryName(patch.getDeliveryName());
            }
            if (patch.getDeliveryStreet() != null) {
                old.setDeliveryStreet(patch.getDeliveryStreet());
            }
            if (patch.getDeliveryCity() != null) {
                old.setDeliveryCity(patch.getDeliveryCity());
            }
            if (patch.getDeliveryState() != null) {
                old.setDeliveryState(patch.getDeliveryState());
            }
            if (patch.getDeliveryZip() != null) {
                old.setDeliveryZip(patch.getDeliveryState());
            }
            if (patch.getCcNumber() != null) {
                old.setCcNumber(patch.getCcNumber());
            }
            if (patch.getCcExpiration() != null) {
                old.setCcExpiration(patch.getCcExpiration());
            }
            if (patch.getCcCVV() != null) {
                old.setCcCVV(patch.getCcCVV());
            }
            orderRepository.save(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long id) {
            orderRepository.deleteById(id);
    }
}
