package com.tacocloud.services;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.domain.TacoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<TacoOrder> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Iterable<TacoOrder> allOrders() {
        return orderRepository.findAll();
    }

    public TacoOrder saveOrder(TacoOrder order) {
        return orderRepository.save(order);
    }

    public Optional<TacoOrder> updateOrder(Long id, TacoOrder order) {
        Optional<TacoOrder> oldOrder = orderRepository.findById(id);
        if(oldOrder.isEmpty()) return Optional.empty();
        oldOrder.ifPresent(old -> order.setUser(old.getUser()));
        oldOrder.ifPresent(old -> order.setTacos(old.getTacos()));
        order.setId(id);
        return Optional.of(orderRepository.save(order));
    }

    public Optional<TacoOrder> patchOrder(Long id, TacoOrder order) {
        Optional<TacoOrder> oldOrder = orderRepository.findById(id);
        if(oldOrder.isPresent()) {
            var old = oldOrder.get();
            if (order.getDeliveryName() != null) {
                old.setDeliveryName(order.getDeliveryName());
            }
            if (order.getDeliveryStreet() != null) {
                old.setDeliveryStreet(order.getDeliveryStreet());
            }
            if (order.getDeliveryCity() != null) {
                old.setDeliveryCity(order.getDeliveryCity());
            }
            if (order.getDeliveryState() != null) {
                old.setDeliveryState(order.getDeliveryState());
            }
            if (order.getDeliveryZip() != null) {
                old.setDeliveryZip(order.getDeliveryState());
            }
            if (order.getCcNumber() != null) {
                old.setCcNumber(order.getCcNumber());
            }
            if (order.getCcExpiration() != null) {
                old.setCcExpiration(order.getCcExpiration());
            }
            if (order.getCcCVV() != null) {
                old.setCcCVV(order.getCcCVV());
            }
            return Optional.of(orderRepository.save(old));
        }
        return Optional.empty();
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
