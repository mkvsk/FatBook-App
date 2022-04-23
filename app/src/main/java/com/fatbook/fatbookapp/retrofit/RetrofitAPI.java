package com.fatbook.fatbookapp.retrofit;

import com.fatbook.fatbookapp.core.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @POST("create")
    Call<Void> saveUser(@Body User user);

    @GET("get-by-pid")
    Call<User> getUser(@Query(value = "pid") Long pid);

    @GET("get-by-login")
    Call<User> getUser(@Query(value = "login") String login);
}
