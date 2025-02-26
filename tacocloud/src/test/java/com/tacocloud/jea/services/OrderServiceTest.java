package com.tacocloud.jea.services;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.domain.Taco;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    public void initService() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(orderRepository);
    }

    @Test
    public void shouldReturnOrderById() {
        var order = testOrder();

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        var receivedOrder = orderService.findOrderById(1L);

        assertThat(receivedOrder.isPresent()).isTrue();
        assertThat(receivedOrder.get()).isEqualTo(order);
    }

    @Test
    public void shouldReturnAllOrders() {
        var order = testOrder();
        var order2 = testOrder();
        var order3 = testOrder();
        var orders = List.of(order, order2, order3);

        when(orderRepository.findAll()).thenReturn(orders);

        var receivedOrders = orderService.allOrders();

        int size = 0;
        for(TacoOrder ord: receivedOrders) {
            size++;
        }

        assertThat(size).isEqualTo(3);
        assertThat(receivedOrders).isEqualTo(orders);
    }

    private TacoOrder testOrder() {
        var tacoOrder = new TacoOrder();

        tacoOrder.setDeliveryName("Bob Test");
        tacoOrder.setDeliveryStreet("4321 South Street");
        tacoOrder.setDeliveryCity("Notrees");
        tacoOrder.setDeliveryState("TX");
        tacoOrder.setDeliveryZip("79759");
        tacoOrder.setCcNumber("4111111111111111");
        tacoOrder.setCcExpiration("10/23");
        tacoOrder.setCcCVV("123");
        var taco = testTaco();
        tacoOrder.addTaco(taco);
        return tacoOrder;
    }

    private Taco testTaco() {
        Taco taco = new Taco();
        taco.setName("Test Taco");
        return taco;
    }
}
