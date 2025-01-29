package com.tacocloud.jea.data;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.OrderRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.data.UserRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import com.tacocloud.domain.TacoOrder;
import com.tacocloud.domain.TacoUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TacoOrderRepositoryTest {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TacoRepository tacoRepository;
    private final IngredientRepository ingredientRepository;
    private TacoOrder savedOrder;
    private TacoUser savedUser;

    @Autowired
    public TacoOrderRepositoryTest(UserRepository userRepository, OrderRepository orderRepository,
                              TacoRepository tacoRepository, IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tacoRepository = tacoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @BeforeEach
    public void setupIngredients() {
        assertThat(orderRepository).isNotNull();
        assertThat(orderRepository.count()).isEqualTo(0);

        var ingr1 = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        var ingr2 = new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN);
        var ingr3 = new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE);

        ingredientRepository.save(ingr1);
        ingredientRepository.save(ingr2);
        ingredientRepository.save(ingr3);

        var user = testUser();
        savedUser = userRepository.save(user);
        var order = testOrder();
        order.setUser(user);
        savedOrder = orderRepository.save(order);

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(tacoRepository.count()).isEqualTo(1);
        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldReturnOrderById() {

        TacoOrder receivedOrder = orderRepository.findById(savedOrder.getId()).get();

        assertThat(receivedOrder).isEqualTo(savedOrder);
    }

    //Ingredients should be the same objects for this test
    @Test
    public void shouldReturnAllOrders() {

        var taco1 = testTaco();
        TacoOrder order = testOrder();
        order.setTacos(List.of(taco1));
        order.setUser(savedUser);
        order = orderRepository.save(order);

        var receivedCollection = orderRepository.findAll();

        assertThat(orderRepository.count()).isEqualTo(2);
        assertThat(tacoRepository.count()).isEqualTo(2);
        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(receivedCollection).isEqualTo(Arrays.asList(savedOrder, order));
    }

    @Test
    public void shouldUpdateExistingOrder() {

        var orderFromDB = orderRepository.findById(savedOrder.getId()).get();
        var taco2 = testTaco();
        orderFromDB.addTaco(taco2);
        orderFromDB.setDeliveryName("NAME CHANGED");

        var receivedOrder = orderRepository.save(orderFromDB);

        taco2.setId(2L);

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(tacoRepository.count()).isEqualTo(2);
        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(1);

        assertThat(receivedOrder.getDeliveryName()).isEqualTo("NAME CHANGED");
        assertThat(receivedOrder).isEqualTo(orderFromDB);
    }

    @Test
    public void shouldDeleteOrder() {

        orderRepository.deleteById(savedOrder.getId());

        assertThat(orderRepository.count()).isEqualTo(0);
        assertThat(tacoRepository.count()).isEqualTo(0);
        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    private TacoUser testUser() {
        TacoUser newUser = new TacoUser("test user", "password");
        newUser.setFullname("Joe Test");
        newUser.setStreet("1234 street");
        newUser.setCity("Unknown");
        newUser.setState("TX");
        newUser.setZip("55555");
        newUser.setPhoneNumber("123-123-1234");
        return newUser;
    }

    private Ingredient testIngredient(String id, String name, Ingredient.Type type) {
        return new Ingredient(id, name, type);
    }

    private Taco testTaco() {
        var taco = new Taco();
        taco.setCreatedAt(LocalDate.now());
        taco.setName("My Awesome Taco");
        taco.getIngredients().add(ingredientRepository.findById("FLTO").get());
        taco.getIngredients().add(ingredientRepository.findById("GRBF").get());
        taco.getIngredients().add(ingredientRepository.findById("JACK").get());
        return taco;
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
}
