package com.tacocloud.services;

import com.tacocloud.data.IngredientRepository;
import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Ingredient;
import com.tacocloud.domain.Taco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TacoService {

    @Value("${taco.tacos.pageSize}")
    private Integer pageSize;
    private final TacoRepository tacoRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public TacoService(TacoRepository tacoRepository, IngredientRepository ingredientRepository) {
        this.tacoRepository = tacoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Iterable<Taco> getRecentTacos() {
        Pageable page = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        return tacoRepository.findAll(page);
    }

    public Optional<Taco> getTacoById(Long tacoId) {
        if(tacoId == null) return Optional.empty();
        return tacoRepository.findById(tacoId);
    }

    @Transactional
    public Optional<Taco> saveTaco(Taco taco) {
        var ingredients = new ArrayList<Ingredient>();
        for(Ingredient ingredient: taco.getIngredients()) {
            if(ingredientRepository.existsById(ingredient.getId())) {
                ingredients.add(ingredient);
            }
        }
        taco.setIngredients(ingredients);
        if(taco.getIngredients().isEmpty()) return Optional.empty();
        return Optional.of(tacoRepository.save(taco));
    }
}
