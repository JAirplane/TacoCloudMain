package com.tacocloud.jea.data;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.domain.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    public void shouldSaveFetchAndDeleteIngredients() {
        assertThat(ingredientRepository).isNotNull();

        assertThat(ingredientRepository.count()).isEqualTo(0);

        ingredientRepository.save(new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP));
        ingredientRepository.save(new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN));
        ingredientRepository.save(new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE));

        assertThat(ingredientRepository.count()).isEqualTo(3);

        assertThat(ingredientRepository.findById("FLTO").get())
                .isEqualTo(new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP));
        assertThat(ingredientRepository.findById("GRBF").get())
                .isEqualTo(new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN));
        assertThat(ingredientRepository.findById("JACK").get())
                .isEqualTo(new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE));

        ingredientRepository.deleteById("FLTO");
        ingredientRepository.deleteById("GRBF");
        ingredientRepository.deleteById("JACK");

        assertThat(ingredientRepository.count()).isEqualTo(0);
    }
}
