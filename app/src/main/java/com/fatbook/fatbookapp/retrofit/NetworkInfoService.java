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

//    @Multipart
//    @POST("upload")
//    Call<Void> createNewUser(
//            @Part("image") MultipartBody.Part file, @Part User user);

    @Multipart
    @POST("test/upload")
    Call<String> createNewUser(@Part MultipartBody.Part image);

    @POST("user/create")
    Call<String> createNewUser(@Body User user, @Query(value = "fat") String fat);

    @Multipart
    @POST("test/upload")
    Call<User> createNewUser(@Part RequestBody user, @Part MultipartBody.Part image);

//    @Part image: MultipartBody.Part,
//    @Part("desc") desc: RequestBody

    @GET("user/get-by-pid")
    Call<User> getUser(@Query(value = "pid") Long pid);

    @GET("user/get-by-login")
    Call<User> getUser(@Query(value = "login") String login);

    @GET("user/check-available-login")
    Call<Void> checkAvailableLogin(@Query(value = "login") String login);

    /**
     *
     * @param ingredient
     *
     */
    @POST("ingredient/add")
    Call<Void> addIngredient(@Body Ingredient ingredient);

    @GET("ingredient/get-all")
    Call<List<Ingredient>> getAllIngredients();

    @GET("feed")
    Call<List<Recipe>> getFeed();

    @GET("user/login")
    Call<User> login(@Query(value = "login") String login, @Query(value = "fat") String fat);

}
