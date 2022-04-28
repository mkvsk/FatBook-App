package com.fatbook.fatbookapp.core;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe implements Serializable {

    private Long pid;

    private String name;

    private String description;

    private User author;

    private List<Ingredient> ingredients;

    private String image;

    private Integer forks;
}
