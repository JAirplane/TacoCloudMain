package com.tacocloud.jea;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.web.api.IngredientController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class IngredientControllerTest {

    private IngredientRepository ingredientRepository;
    private WebTestClient testClient;

    @BeforeEach
    public void initialization() {
        ingredientRepository = Mockito.mock(IngredientRepository.class);
        testClient = WebTestClient.bindToController(new IngredientController(ingredientRepository))
                .build();
    }

    @Test
    public void shouldReturnIngredientById() {
        var ingredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP);
        var ingredientMono = Mono.just(ingredient);
        when(ingredientRepository.findById("TEST")).thenReturn(ingredientMono);

        testClient.get().uri("/api/ingredients/TEST")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ingredient.class)
                .isEqualTo(ingredient);
    }

    @Test
    public void shouldReturnAllIngredients() {
        Ingredient[] ingredients = {
                new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP),
                new Ingredient("TEST2", "Test Ingredient2", Ingredient.Type.VEGGIES),
                new Ingredient("TEST3", "Test Ingredient3", Ingredient.Type.PROTEIN)
        };
        var ingredientsFlux = Flux.just(ingredients);

        when(ingredientRepository.findAll()).thenReturn(ingredientsFlux);

        testClient.get().uri("/api/ingredients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Ingredient.class)
                .contains(Arrays.copyOf(ingredients, 3));
    }

    @Test
    public void shouldReturnSavedIngredient() {
        var unsavedIngredientMono = Mono.just(new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP));
        var savedIngredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP);
        var savedIngredientMono = Mono.just(savedIngredient);

        when(ingredientRepository.save(any())).thenReturn(savedIngredientMono);

        testClient.post().uri("/api/ingredients")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedIngredientMono, Ingredient.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Ingredient.class)
                .isEqualTo(savedIngredient);
    }

    @Test
    public void shouldReturnUpdatedIngredient() {
        var unsavedIngredientMono = Mono.just(new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP));
        var savedIngredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.WRAP);
        var savedIngredientMono = Mono.just(savedIngredient);

        when(ingredientRepository.save(any())).thenReturn(savedIngredientMono);

        testClient.put().uri("/api/ingredients/TEST")
                .accept(MediaType.APPLICATION_JSON)
                .body(unsavedIngredientMono, Ingredient.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ingredient.class)
                .isEqualTo(savedIngredient);
    }

    @Test
    public void shouldDeleteIngredient() {

        Mockito.when(ingredientRepository.deleteById("TEST")).thenReturn(Mono.empty());

        testClient.delete().uri("/api/ingredients/TEST")
                .exchange()
                .expectStatus().isNoContent();
    }
}
