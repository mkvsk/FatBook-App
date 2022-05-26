package com.fatbook.fatbookapp.retrofit;

import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkInfoService {

    /**
     * User
     */
    @GET("user/get/login")
    Call<User> getUser(@Query(value = "login") String login);

    @POST("user/create")
    Call<User> userCreate(@Body User user, @Query(value = "fat") String fat);

    @GET("user/login/check")
    Call<Boolean> loginCheck(@Query(value = "login") String login);

    @GET("user/signin")
    Call<User> signIn(@Query(value = "login") String login, @Query(value = "fat") String fat);

    /**
     * Ingredients
     */
    @POST("ingredient/add")
    Call<Void> addIngredient(@Body Ingredient ingredient);

    @GET("ingredient/get/all")
    Call<List<Ingredient>> getAllIngredients();

    /**
     * Recipe
     */
    @POST("recipe/create")
    Call<Void> recipeCreate(@Body Recipe recipe);

    @POST("recipe/update")
    Call<Void> recipeUpdate(@Body Recipe recipe);

    @POST("recipe/delete")
    Call<Void> recipeDelete(@Body Recipe recipe);

    /**
     * Feed
     */
    @GET("recipe/feed")
    Call<List<Recipe>> getFeed(@Query(value = "pid") Long pid);

    //========================================================================================

//    @Multipart
//    @POST("upload")
//    Call<Void> createNewUser(
//            @Part("image") MultipartBody.Part file, @Part User user);

    @Multipart
    @POST("test/upload")
    Call<String> createNewUser(@Part MultipartBody.Part image);

    @Multipart
    @POST("test/upload")
    Call<User> createNewUser(@Part RequestBody user, @Part MultipartBody.Part image);

}
