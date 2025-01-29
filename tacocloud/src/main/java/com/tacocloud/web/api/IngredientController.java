package com.tacocloud.web.api;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/ingredients", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class IngredientController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable("id") String id) {
        var ingredientOptional = ingredientService.findIngredientById(id);
        return ingredientOptional.map(
                ingredient -> new ResponseEntity<>(ingredient, HttpStatus.OK))
                                    .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public Iterable<Ingredient> getAllIngredients() {
        return ingredientService.allIngredients();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Ingredient> saveIngredient(@RequestBody Ingredient ingredient) {
        var ingredientOptional =  ingredientService.saveIngredient(ingredient);
        return ingredientOptional.map(
                        ingr -> new ResponseEntity<>(ingr, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.FOUND));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIngredient(@PathVariable("id") String id) {
        ingredientService.deleteIngredientById(id);
    }
}
