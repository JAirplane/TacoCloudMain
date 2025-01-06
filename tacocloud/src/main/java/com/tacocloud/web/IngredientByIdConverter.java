package com.tacocloud.web;

import com.tacocloud.data.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import com.tacocloud.domain.Ingredient;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientByIdConverter(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Convert the ingredientId object of type {@code S} to target type {@code T}.
     *
     * @param ingredientId the ingredientId object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the ingredientId cannot be converted to the desired target type
     */
    @Override
    public Ingredient convert(@Nullable String ingredientId) {
        if(ingredientId == null) return null;
        var taco = ingredientRepository.findById(ingredientId);
        return taco.orElse(null);
    }
}
