package com.fatbook.fatbookapp.core;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

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

    private Date regDate;

    private Role role;

    private String photo;

    private Uri uri;

//    private List<Recipe> recipes;

//    private List<Recipe> recipesForked;

//    private List<Recipe> recipesSaved;
}
