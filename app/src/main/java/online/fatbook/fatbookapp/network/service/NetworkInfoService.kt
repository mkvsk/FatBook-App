package online.fatbook.fatbookapp.network.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import online.fatbook.fatbookapp.core.DeleteRequest
import online.fatbook.fatbookapp.network.AuthenticationRequest
import online.fatbook.fatbookapp.network.AuthenticationResponse
import online.fatbook.fatbookapp.network.LoginResponse
import online.fatbook.fatbookapp.core.recipe.*
import online.fatbook.fatbookapp.core.recipe.ingredient.Ingredient
import online.fatbook.fatbookapp.core.recipe.ingredient.unit.IngredientUnit
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.network.UserUpdateRequest
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

    @POST("auth/recover")
    fun recoverPassword(@Query(value = "identifier") identifier: String?): Call<AuthenticationResponse>

    @POST("auth/change")
    fun changePassword(
            @Query(value = "username") username: String?,
            @Query(value = "password") password: String?
    ): Call<AuthenticationResponse>

    /**
     * User
     */
    @GET("user/get/username")
    fun getUserByUsername(@Query(value = "username") username: String?): Call<User>

    @POST("user/update")
    fun updateUser(@Body request: UserUpdateRequest?): Call<User>

    /**
     * Static data
     */
    @GET("data/ingredient/get/all")
    fun getAllIngredients(): Call<List<Ingredient>>

    @GET("data/category/get/all")
    fun getAllCookingCategories(): Call<List<CookingCategory>>

    @GET("data/method/get/all")
    fun getAllCookingMethods(): Call<List<CookingMethod>>

    @GET("data/difficulty/get/all")
    fun getAllCookingDifficulties(): Call<List<CookingDifficulty>>

    @GET("data/unit/get/all")
    fun getAllIngredientUnits(): Call<List<IngredientUnit>>

    /**
     * Image service
     */
    @Multipart
    @POST("upload")
    fun imgUpload(@Part file: MultipartBody.Part?, @Query(value = "type") type: String, @Query(value = "id") id: String, @Query(value = "step") step: String): Call<String>

    @POST("delete")
    fun imgDelete(@Body request: DeleteRequest): Call<Void>

    /**
     * Feed
     */
    @GET("feed")
    fun feed(@Query(value = "username") username: String?, @Query(value = "pid") pid: Long?): Call<List<RecipeSimpleObject>>

    /**
     * Recipe
     */
    @POST("recipe/create")
    fun recipeCreate(@Body recipe: Recipe): Call<Void>

//    ==========================================================================================

    @POST("user/create")
    fun userCreate(@Body user: User?, @Query(value = "fat") fat: String?): Call<User>

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