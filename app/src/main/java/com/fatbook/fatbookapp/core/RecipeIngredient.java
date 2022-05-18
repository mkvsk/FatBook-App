package com.fatbook.fatbookapp.core;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredient implements Serializable {

    private Long pid;

    private Recipe recipe;

    private Ingredient ingredient;

    private IngredientUnit unit;

    private double quantity;
}
