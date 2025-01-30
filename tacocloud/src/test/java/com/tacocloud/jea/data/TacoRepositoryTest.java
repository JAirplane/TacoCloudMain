package com.tacocloud.jea.data;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class TacoRepositoryTest {

    private final TacoRepository tacoRepository;
    private final IngredientRepository ingredientRepository;

    private List<Ingredient> ingredients;

    @Autowired
    public TacoRepositoryTest(TacoRepository tacoRepository, IngredientRepository ingredientRepository,
                              EntityManager entityManager) {
        this.tacoRepository = tacoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @BeforeEach
    public void setupIngredients() {
        ingredients = new ArrayList<>();
        var ingr1 = new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP);
        var ingr2 = new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN);
        var ingr3 = new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE);
        assertThat(ingredients.size()).isEqualTo(0);

        ingredients.add(ingr1);
        ingredients.add(ingr2);
        ingredients.add(ingr3);
        ingredientRepository.save(ingr1);
        ingredientRepository.save(ingr2);
        ingredientRepository.save(ingr3);

        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldSaveTaco() {

        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        taco.setIngredients(ingredients);

        Taco receivedTaco = tacoRepository.save(taco);

        assertThat(receivedTaco.getIngredients().size()).isEqualTo(3);
        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteTacoAndNotTheIngredients() {

        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        taco.setIngredients(ingredients);

        taco = tacoRepository.save(taco);

        tacoRepository.deleteById(taco.getId());

        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldDeleteIngredientFromTaco() {

        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        taco.setIngredients(ingredients);

        taco = tacoRepository.save(taco);

        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(1);

        taco.getIngredients().removeFirst();

        taco = tacoRepository.save(taco);

        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(1);
        assertThat(taco.getIngredients().size()).isEqualTo(2);
    }

    @Test
    public void shouldAddIngredientToTaco() {

        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        taco.setIngredients(ingredients);

        taco = tacoRepository.save(taco);

        assertThat(ingredientRepository.count()).isEqualTo(3);
        assertThat(tacoRepository.count()).isEqualTo(1);

        Ingredient testIngredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.SAUCE);
        ingredientRepository.save(testIngredient);
        taco.getIngredients().add(testIngredient);
        tacoRepository.save(taco);

        assertThat(ingredientRepository.count()).isEqualTo(4);
        assertThat(taco.getIngredients().size()).isEqualTo(4);
    }
}
