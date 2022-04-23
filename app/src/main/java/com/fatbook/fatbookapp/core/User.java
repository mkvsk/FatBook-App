package com.fatbook.fatbookapp.core;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long pid;

    private String name;

    private String login;

    private Date birthday;

    private String bio;

    private Date regDate;

    private Role role;

//    private List<Recipe> recipes;

//    private List<Recipe> recipesForked;

//    private List<Recipe> recipesSaved;
}
