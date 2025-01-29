package com.tacocloud.services;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final TacoRepository tacoRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository,
                             TacoRepository tacoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
    }

    public Optional<Ingredient> findIngredientById(String id) {
        return ingredientRepository.findById(id);
    }

    public Iterable<Ingredient> allIngredients() {
        return ingredientRepository.findAll();
    }

    @Transactional
    public Optional<Ingredient> saveIngredient(Ingredient ingredient) {
        if(ingredientRepository.existsById(ingredient.getId())) {
            return Optional.empty();
        }
        return Optional.of(ingredientRepository.save(ingredient));
    }

    @Transactional
    public void deleteIngredientById(String id) {
        var ingredientOptional = ingredientRepository.findById(id);
        if(ingredientOptional.isPresent()) {
            var ingredient = ingredientOptional.get();
            var tacos = tacoRepository.findAll();
            for(Taco taco: tacos) {
                if(taco.getIngredients().contains(ingredient)) {
                    taco.getIngredients().remove(ingredient);
                    if(taco.getIngredients().isEmpty()) {
                        tacoRepository.delete(taco);
                    }
                    else {
                        tacoRepository.save(taco);
                    }
                }
            }
            ingredientRepository.delete(ingredient);
        }
    }
}
