package com.tacocloud.jea.services;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import com.tacocloud.services.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class IngredientServiceTest {

    private IngredientRepository ingredientRepository;
    private TacoRepository tacoRepository;
    private Ingredient ingredient;
    private IngredientService ingredientService;

    @BeforeEach
    public void initService() {
        ingredientRepository = Mockito.mock(IngredientRepository.class);
        tacoRepository = Mockito.mock(TacoRepository.class);
        ingredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.CHEESE);
        ingredientService = new IngredientService(ingredientRepository, tacoRepository);
    }

    @Test
    public void shouldReturnIngredientById() {

        when(ingredientRepository.findById("TEST")).thenReturn(Optional.of(ingredient));
        var ingredientById = ingredientService.findIngredientById("TEST");
        assertThat(ingredientById.isPresent()).isTrue();
        assertThat(ingredientById.get()).isEqualTo(ingredient);
    }

    @Test
    public void shouldReturnEmptyIngredientById() {
        when(ingredientRepository.findById("WRONG")).thenReturn(Optional.empty());
        var ingredientById = ingredientService.findIngredientById("WRONG");
        assertThat(ingredientById.isPresent()).isFalse();
    }

    @Test
    public void shouldReturnAllIngredients() {
        Iterable<Ingredient> ingredients = List.of(
                new Ingredient("TEST1", "Test1", Ingredient.Type.WRAP),
                new Ingredient("TEST2", "Test2", Ingredient.Type.WRAP),
                new Ingredient("TEST3", "Test3", Ingredient.Type.WRAP));
        when(ingredientRepository.findAll()).thenReturn(ingredients);

        var serviceIngredients = ingredientService.allIngredients();

        assertThat(serviceIngredients).isEqualTo(ingredients);
    }

    @Test
    public void shouldReturnSavedIngredient() {
        when(ingredientRepository.save(any())).thenReturn(ingredient);

        var saved = ingredientService.saveIngredient(ingredient);

        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get()).isEqualTo(ingredient);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenIngredientInDB() {
        when(ingredientRepository.existsById(any())).thenReturn(true);

        var saved = ingredientService.saveIngredient(ingredient);

        assertThat(saved.isPresent()).isFalse();
    }

    @Test
    public void shouldDeleteIngredientFromTaco() {
        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        taco.addIngredient(ingredient);

        when(ingredientRepository.findById(any())).thenReturn(Optional.ofNullable(ingredient));
        when(tacoRepository.findAll()).thenReturn(List.of(taco));

        ingredientService.deleteIngredientById("TEST");

        assertThat(taco.getIngredients().contains(ingredient)).isFalse();
    }
}
