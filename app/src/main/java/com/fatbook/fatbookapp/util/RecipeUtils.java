package com.fatbook.fatbookapp.util;

import com.fatbook.fatbookapp.core.Recipe;

import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.SneakyThrows;

public class RecipeUtils {

    public static final String TAG_BOOKMARKS_CHECKED = "tag_bookmarks_checked";
    public static final String TAG_BOOKMARKS_UNCHECKED = "tag_bookmarks_unchecked";

    public static final String TAG_FORK_CHECKED = "tag_fork_checked";
    public static final String TAG_FORK_UNCHECKED = "tag_fork_unchecked";

    public static SimpleDateFormat regDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss",Locale.US);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd",Locale.US);

//    public String stripIngredientQuantity(double value) {
//        if (StringUtils.endsWith(String.valueOf(value), "0")) {
//
//        }
//    }

    @SneakyThrows
    public static void saveRecipe(Recipe recipe) {
        //TODO save recipe
    }

    @SneakyThrows
    public static void deleteRecipe(Recipe recipe) {
        //TODO save recipe
    }
}
