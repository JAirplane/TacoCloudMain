package com.tacocloud.jea;

import com.tacocloud.data.OrderRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.domain.TacoUser;
import com.tacocloud.messaging.OrderMessagingService;
import com.tacocloud.web.api.OrderApiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderApiControllerTest {

    private OrderRepository orderRepository;
    private TacoRepository tacoRepository;
    private OrderMessagingService msgService;
    private WebTestClient testClient;

    @BeforeEach
    public void initialization() {
        orderRepository = Mockito.mock(OrderRepository.class);
        tacoRepository = Mockito.mock(TacoRepository.class);
        msgService = Mockito.mock(OrderMessagingService.class);

        testClient = WebTestClient.bindToController(
                new OrderApiController(orderRepository, tacoRepository, msgService))
                .build();
    }

    @Test
    public void shouldReturnTacoOrderById() {
        var order = testOrder(1L);
        var orderMono = Mono.just(order);

        when(orderRepository.findById(1L)).thenReturn(orderMono);

        testClient.get().uri("/api/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TacoOrder.class)
                .isEqualTo(order);
    }

    @Test
    public void shouldReturnNullTacoOrderById() {

        when(orderRepository.findById(1L)).thenReturn(Mono.empty());

        testClient.get().uri("/api/orders/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnAllOrders() {
        TacoOrder[] orders = {testOrder(1L), testOrder(2L), testOrder(3L)};
        var ordersFlux = Flux.just(orders);

        when(orderRepository.findAll()).thenReturn(ordersFlux);

        testClient.get().uri("/api/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TacoOrder.class)
                .contains(orders);
    }

    @Test
    public void shouldReturnPostedOrder() {
        var unsavedOrderMono = Mono.just(testOrder(null));
        var savedOrder = testOrder(null);
        var savedOrderMono = Mono.just(savedOrder);

        when(orderRepository.save(any())).thenReturn(savedOrderMono);

        testClient.post().uri("/api/orders")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedOrderMono, TacoOrder.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TacoOrder.class)
                .isEqualTo(savedOrder);
    }

    @Test
    public void shouldReturnUpdatedOrder() {
        var unsavedOrderMono = Mono.just(testOrder(null));
        var savedOrder = testOrder(1L);
        var savedOrderMono = Mono.just(savedOrder);

        when(orderRepository.save(any())).thenReturn(savedOrderMono);
        when(orderRepository.findById(1L)).thenReturn(savedOrderMono);

        testClient.put().uri("/api/orders/1")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedOrderMono, TacoOrder.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TacoOrder.class)
                .isEqualTo(savedOrder);
    }

    @Test
    public void shouldReturnNotFoundOnPutTacoOrder() {
        var unsavedOrderMono = Mono.just(testOrder(1L));

        when(orderRepository.findById(1L)).thenReturn(Mono.empty());

        testClient.put().uri("/api/orders/1")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedOrderMono, TacoOrder.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnPatchedTacoOrder() {
        var unsavedOrder = testOrder(1L);
        unsavedOrder.setDeliveryState("New state");
        unsavedOrder.setDeliveryName("New Name");
        unsavedOrder.setDeliveryStreet("New Street");
        var unsavedOrderMono = Mono.just(unsavedOrder);

        var savedOrderMono = Mono.just(testOrder(1L));

        when(orderRepository.findById(1L)).thenReturn(savedOrderMono);
        when(orderRepository.save(any())).thenReturn(savedOrderMono);

        testClient.patch().uri("/api/orders/1")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedOrderMono, TacoOrder.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TacoOrder.class)
                .isEqualTo(unsavedOrder);
    }

    @Test
    public void shouldReturnNotFoundOnPatchTacoOrder() {
        var unsavedOrderMono = Mono.just(testOrder(1L));

        when(orderRepository.findById(1L)).thenReturn(Mono.empty());

        testClient.patch().uri("/api/orders/1")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedOrderMono, TacoOrder.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnNoContentOnDeleteTacoOrder() {
        when(orderRepository.deleteById(1L)).thenReturn(Mono.empty());

        testClient.delete().uri("/api/orders/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    private TacoOrder testOrder(Long id) {
        var taco = new Taco();
        taco.setId(1L);
        taco.setName("Taco " + taco.getId());
        var ingredientA = new Ingredient("INGA", "Ingredient A", Ingredient.Type.WRAP);
        var ingredientB = new Ingredient("INGB", "Ingredient B", Ingredient.Type.VEGGIES);
        taco.addIngredient(ingredientA);
        taco.addIngredient(ingredientB);

        var taco2 = new Taco();
        taco2.setId(2L);
        taco2.setName("Taco " + taco.getId());
        var ingredientA2 = new Ingredient("INGC", "Ingredient C", Ingredient.Type.WRAP);
        var ingredientB2 = new Ingredient("INGD", "Ingredient D", Ingredient.Type.VEGGIES);
        taco2.addIngredient(ingredientA2);
        taco2.addIngredient(ingredientB2);

//        TacoUser user = new TacoUser("user", "password", "Test User",
//                "Test street", "Test City", "Test State", "00000",
//                "+115555555");
//        user.setId(1L);

        var tacoOrder = new TacoOrder();
        tacoOrder.setId(id);
        tacoOrder.setDeliveryName("Test delivery");
        tacoOrder.setDeliveryStreet("Test street");
        tacoOrder.setDeliveryCity("Test city");
        tacoOrder.setDeliveryState("Test state");
        tacoOrder.setDeliveryZip("00000");
        tacoOrder.setCcNumber("55555555555555555555");
        tacoOrder.setCcExpiration("01/30");
        tacoOrder.setCcCVV("111");
        tacoOrder.addTaco(taco);
        tacoOrder.addTaco(taco2);
//        tacoOrder.setUser(user);
//        user.addOrder(tacoOrder);
        return tacoOrder;
    }
}
