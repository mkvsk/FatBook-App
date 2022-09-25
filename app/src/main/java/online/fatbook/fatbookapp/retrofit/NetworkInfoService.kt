package online.fatbook.fatbookapp.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.core.*
import online.fatbook.fatbookapp.core.user.User
import retrofit2.Call
import retrofit2.http.*

interface NetworkInfoService {

    /**
     * Authentication
     */
    @POST("auth/vc/confirm")
    fun confirmVCode(
        @Query(value = "email") email: String?,
        @Query(value = "code") code: String?
    ): Call<AuthenticationResponse>

    @GET("auth/username/check")
    fun usernameCheck(@Query(value = "username") username: String?): Call<Boolean>

    @GET("auth/vc/send")
    fun emailCheck(@Query(value = "email") email: String?): Call<AuthenticationResponse>

    @POST("auth/register")
    fun register(@Body request: AuthenticationRequest?): Call<AuthenticationResponse>

    @POST("login")
    fun login(@Body request: RequestBody?): Call<LoginResponse>

    /**
     * User
     */
    @GET("user/get/login")
    fun getUser(@Query(value = "login") login: String?): Call<User>

    @POST("user/create")
    fun userCreate(@Body user: User?, @Query(value = "fat") fat: String?): Call<User>

    @POST("user/update")
    fun userUpdate(@Body user: User?): Call<User>

    @GET("user/login/check")
    fun loginCheck(@Query(value = "login") login: String?): Call<Boolean>

    @Multipart
    @POST("user/upload")
    fun uploadUserImage(
        @Part image: MultipartBody.Part?,
        @Query(value = "dir") dir: String?,
        @Query(value = "login") login: String?
    ): Call<User?>

    @GET("user/bookmarks")
    fun getUserBookmarks(@Query(value = "login") login: String?): Call<ArrayList<Recipe>?>

    /**
     * Ingredients
     */
    @POST("ingredient/add")
    fun addIngredient(@Body ingredient: Ingredient?): Call<Void>

    @GET("ingredient/get/all")
    fun allIngredients(): Call<List<Ingredient>>

    /**
     * Recipe
     */
    @POST("recipe/create")
    fun recipeCreate(@Body recipe: Recipe?): Call<Recipe>

    @POST("recipe/update")
    fun recipeUpdate(@Body recipe: Recipe?): Call<Recipe>

    @POST("recipe/delete")
    fun recipeDelete(@Body recipe: Recipe?): Call<Void>

    @POST("recipe/forked")
    fun recipeForked(
        @Query(value = "pidUser") pidUser: Long?,
        @Query(value = "pidRecipe") pidRecipe: Long?,
        @Query(value = "forked") forked: Boolean?
    ): Call<Recipe>

    @POST("recipe/bookmarked")
    fun recipeBookmarked(
        @Query(value = "pidUser") pidUser: Long?,
        @Query(value = "pidRecipe") pidRecipe: Long?,
        @Query(value = "bookmarked") bookmark: Boolean?
    ): Call<Recipe>

    @Multipart
    @POST("recipe/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Query(value = "dir") dir: String?,
        @Query(value = "id") id: Long?
    ): Call<Recipe>

    /**
     * Feed
     */
    @GET("recipe/feed")
    fun getFeed(@Query(value = "pid") pid: Long?): Call<ArrayList<Recipe>?>

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
    fun createNewUser(@Part image: MultipartBody.Part?): Call<String>?

    @Multipart
    @POST("test/upload")
    fun createNewUser(@Part user: RequestBody?, @Part image: MultipartBody.Part?): Call<User>?

}