package online.fatbook.fatbookapp.retrofit;

import online.fatbook.fatbookapp.core.Ingredient;
import online.fatbook.fatbookapp.core.Recipe;
import online.fatbook.fatbookapp.core.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NetworkInfoService {

    /**
     * User
     */
    @GET("user/get/login")
    Call<User> getUser(@Query(value = "login") String login);

    @POST("user/create")
    Call<User> userCreate(@Body User user, @Query(value = "fat") String fat);

    @POST("user/update")
    Call<User> userUpdate(@Body User user);

    @GET("user/login/check")
    Call<Boolean> loginCheck(@Query(value = "login") String login);

    @GET("user/signin")
    Call<User> signIn(@Query(value = "login") String login, @Query(value = "fat") String fat);

    @Multipart
    @POST("user/upload")
    Call<User> uploadUserImage(@Part MultipartBody.Part image, @Query(value = "dir") String dir, @Query(value = "login") String login);

    @GET("user/bookmarks")
    Call<List<Recipe>> getUserBookmarks(@Query(value = "login") String login);

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
    Call<Recipe> recipeCreate(@Body Recipe recipe);

    @POST("recipe/update")
    Call<Recipe> recipeUpdate(@Body Recipe recipe);

    @POST("recipe/delete")
    Call<Void> recipeDelete(@Body Recipe recipe);

    @POST("recipe/forked")
    Call<Recipe> recipeForked(@Query(value = "pidUser") Long pidUser, @Query(value = "pidRecipe") Long pidRecipe, @Query(value = "forked") Boolean forked);

    @POST("recipe/bookmarked")
    Call<Recipe> recipeBookmarked(@Query(value = "pidUser") Long pidUser, @Query(value = "pidRecipe") Long pidRecipe, @Query(value = "bookmarked") Boolean bookmark);

    @Multipart
    @POST("recipe/upload")
    Call<Recipe> uploadImage(@Part MultipartBody.Part image, @Query(value = "dir") String dir, @Query(value = "id") Long id);
    /**
     * Feed
     */
    @GET("recipe/feed")
    Call<List<Recipe>> getFeed(@Query(value = "pid") Long pid);

    /**
     * File upload
     */

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
