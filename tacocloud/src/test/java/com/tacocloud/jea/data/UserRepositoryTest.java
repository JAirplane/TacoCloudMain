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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TacoRepository tacoRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, OrderRepository orderRepository,
                              TacoRepository tacoRepository, IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tacoRepository = tacoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @BeforeEach
    public void setupIngredients() {
        var ingr1 = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        var ingr2 = new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN);
        var ingr3 = new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE);

        ingredientRepository.save(ingr1);
        ingredientRepository.save(ingr2);
        ingredientRepository.save(ingr3);
    }

    @Test
    public void shouldSaveUser() {
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(0);

        TacoUser newUser = testUser();

        userRepository.save(newUser);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldUpdateUser() {
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(0);

        TacoUser newUser = testUser();

        newUser = userRepository.save(newUser);

        assertThat(userRepository.count()).isEqualTo(1);

        newUser.setFullname("Name changed");

        TacoUser receivedUser = userRepository.save(newUser);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findById(receivedUser.getId()).get()).isEqualTo(newUser);
    }

    @Test
    public void shouldSaveUserAndHisOrders() {
        var user = testUser();
        var order = testOrder();
        user.addOrder(order);

        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(0);

        userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(tacoRepository.count()).isEqualTo(1);
        assertThat(ingredientRepository.count()).isEqualTo(3);
    }

    @Test
    public void shouldDeleteUserAndHisOrders() {
        var user = testUser();
        var order = testOrder();
        user.addOrder(order);

        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(0);

        user = userRepository.save(user);

        userRepository.deleteById(user.getId());

        assertThat(userRepository.count()).isEqualTo(0);
        assertThat(orderRepository.count()).isEqualTo(0);
        assertThat(tacoRepository.count()).isEqualTo(0);
        assertThat(ingredientRepository.count()).isEqualTo(3);
    }

    @Test
    public void shouldUpdateUserOrders() {
        var user = testUser();
        var order = testOrder();
        user.addOrder(order);

        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(0);

        user = userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(tacoRepository.count()).isEqualTo(1);
        assertThat(ingredientRepository.count()).isEqualTo(3);

        var anotherOrder = testOrder();
        user.addOrder(anotherOrder);

        userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(2);
        assertThat(tacoRepository.count()).isEqualTo(2);
        assertThat(ingredientRepository.count()).isEqualTo(3);
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

        var taco = testTaco();

        var tacoOrder = new TacoOrder();

        tacoOrder.setDeliveryName("Bob Test");
        tacoOrder.setDeliveryStreet("4321 South Street");
        tacoOrder.setDeliveryCity("Notrees");
        tacoOrder.setDeliveryState("TX");
        tacoOrder.setDeliveryZip("79759");
        tacoOrder.setCcNumber("4111111111111111");
        tacoOrder.setCcExpiration("10/23");
        tacoOrder.setCcCVV("123");
        List<Taco> tacos = new ArrayList<>();
        tacos.add(taco);
        tacoOrder.setTacos(tacos);

        return tacoOrder;
    }
}
