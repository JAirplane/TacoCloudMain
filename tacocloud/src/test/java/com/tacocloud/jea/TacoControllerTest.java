package com.tacocloud.jea;

import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import com.tacocloud.web.PagingProps;
import com.tacocloud.web.api.TacoController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TacoControllerTest {

    private static TacoRepository tacoRepository;
    private static WebTestClient webTestClient;

    @BeforeAll
    public static void initialization() {
        tacoRepository = Mockito.mock(TacoRepository.class);
        webTestClient = WebTestClient.bindToController(
                new TacoController(tacoRepository, new PagingProps())).build();
    }

    @Test
    public void shouldReturnRecentTacos() {
        Taco[] tacos = {
                testTaco(1L), testTaco(2L),
                testTaco(3L), testTaco(4L),
                testTaco(5L), testTaco(6L),
                testTaco(7L), testTaco(8L),
                testTaco(9L), testTaco(10L),
                testTaco(11L), testTaco(12L),
                testTaco(13L), testTaco(14L),
                testTaco(15L), testTaco(16L)};
        Flux<Taco> tacoFlux = Flux.just(tacos);
        when(tacoRepository.findAll()).thenReturn(tacoFlux);
        webTestClient.get().uri("/api/tacos?recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Taco.class)
                .contains(Arrays.copyOf(tacos, 12));
    }

    @Test
    public void shouldReturnTacoById() {
        var taco = testTaco(1L);
        var tacoMono = Mono.just(taco);
        when(tacoRepository.findById(1L)).thenReturn(tacoMono);
        webTestClient.get().uri("/api/tacos/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Taco.class)
                .consumeWith(response -> {
                    var tacoAns = response.getResponseBody();
                    assert tacoAns != null;
                    assertEquals(tacoAns, taco);
                });
    }

    @Test
    public void shouldReturnPostedTaco() {
        var unsavedTacoMono = Mono.just(testTaco(null));
        var savedTaco = testTaco(null);
        var savedTacoMono = Mono.just(savedTaco);

        when(tacoRepository.save(any())).thenReturn(savedTacoMono);

        webTestClient.post().uri("/api/tacos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(unsavedTacoMono, Taco.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Taco.class)
                .isEqualTo(savedTaco);
    }

    private Taco testTaco(Long id) {
        var taco = new Taco();
        taco.setId(id);
        taco.setName("Taco " + id);
        var ingredientA = new Ingredient("INGA", "Ingredient A", Ingredient.Type.WRAP);
        var ingredientB = new Ingredient("INGB", "Ingredient B", Ingredient.Type.VEGGIES);
        taco.addIngredient(ingredientA);
        taco.addIngredient(ingredientB);
        return taco;
    }
}
