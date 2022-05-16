package com.fatbook.fatbookapp.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Long pid;

    private String name;

    private String login;

    private Date birthday;

    private String bio;

    private Role role;

    private String image;

    private Date regDate;

    private List<Recipe> recipes;

    private List<Recipe> recipesForked;

    private List<Recipe> recipesSaved;

    private List<Recipe> forkedRecipes;
}
