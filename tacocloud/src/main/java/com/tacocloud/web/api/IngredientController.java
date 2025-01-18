package com.tacocloud.web.api;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.domain.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping(path = "/api/ingredients", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping(path = "/{id}")
    public Mono<Ingredient> getIngredientById(@PathVariable("id") String id) {
        return ingredientRepository.findById(id);
    }

    @GetMapping
    public Flux<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Ingredient>> saveIngredient(@RequestBody Mono<Ingredient> ingredient) {
        return ingredient.flatMap(ingredientRepository::save)
                .map(i -> {
                    return new ResponseEntity<Ingredient>(i, HttpStatus.CREATED);
                });
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Ingredient> updateIngredient(@PathVariable("id") String id, @RequestBody Mono<Ingredient> ingredient) {
        return ingredient.flatMap(ingredientRepository::save);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIngredient(@PathVariable("id") String id) {
        return ingredientRepository.deleteById(id);
    }
}
