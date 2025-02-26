package com.tacocloud.jea.services;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import com.tacocloud.services.TacoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TacoServiceTest {

    int pageSize = 3;
    private IngredientRepository ingredientRepository;
    private TacoRepository tacoRepository;
    private TacoService tacoService;

    @BeforeEach
    public void initService() {
        ingredientRepository = Mockito.mock(IngredientRepository.class);
        tacoRepository = Mockito.mock(TacoRepository.class);
        tacoService = new TacoService(tacoRepository, ingredientRepository);
        ReflectionTestUtils.setField(tacoService, "pageSize", pageSize);
    }

    @Test
    public void shouldReturnTacosAmountFromYML() {
        List<Taco> tacos = new ArrayList<>();
        for(int i = 0; i < pageSize; i++) {
            tacos.add(testTaco());
        }
        when(tacoRepository.findAll((Pageable) any())).thenReturn(new PageImpl<>(tacos));

        Iterable<Taco> recentTacos = tacoService.getRecentTacos();
        int size = 0;
        for (Taco recentTaco : recentTacos) {
            size++;
        }
        assertThat(size).isEqualTo(pageSize);
    }

    @Test
    public void shouldReturnTacoById() {
        Taco taco = testTaco();
        taco.setId(1L);

        when(tacoRepository.findById(any())).thenReturn(Optional.of(taco));

        var returnedTaco = tacoService.getTacoById(1L);

        assertThat(returnedTaco.isPresent()).isTrue();
        assertThat(returnedTaco.get().getId()).isEqualTo(1L);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenReturnTacoWithNullId() {
        when(tacoRepository.findById(any())).thenReturn(Optional.empty());

        var returnedTaco = tacoService.getTacoById(null);

        assertThat(returnedTaco.isPresent()).isFalse();
    }

    @Test
    public void shouldReturnSavedTaco() {
        var ingredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.CHEESE);
        var taco = testTaco();
        taco.addIngredient(ingredient);

        when(ingredientRepository.existsById(any())).thenReturn(true);
        when(tacoRepository.save(any())).thenReturn(taco);

        var savedTaco = tacoService.saveTaco(taco);

        assertThat(savedTaco.isPresent()).isTrue();
        assertThat(savedTaco.get().equals(taco)).isTrue();
    }

    @Test
    public void shouldReturnTacoWithExistedIngredient() {
        var ingredient = new Ingredient("TEST", "Test Ingredient", Ingredient.Type.CHEESE);
        var ingredient2 = new Ingredient("TEST2", "Test Ingredient 2", Ingredient.Type.CHEESE);
        var taco = testTaco();
        taco.addIngredient(ingredient);
        taco.addIngredient(ingredient2);

        when(ingredientRepository.existsById("TEST")).thenReturn(true);
        when(ingredientRepository.existsById("TEST2")).thenReturn(false);
        when(tacoRepository.save(any())).thenReturn(taco);

        var savedTaco = tacoService.saveTaco(taco);

        assertThat(savedTaco.isPresent()).isTrue();
        assertThat(savedTaco.get().getIngredients().size()).isEqualTo(1);
        assertThat(savedTaco.get().equals(taco)).isTrue();
    }

    @Test
    public void shouldReturnEmptyOptional() {
        var taco = testTaco();

        var savedTaco = tacoService.saveTaco(taco);

        assertThat(savedTaco.isPresent()).isFalse();
    }

    private Taco testTaco() {
        Taco taco = new Taco();
        taco.setName("My Awesome Taco");
        return taco;
    }
}
