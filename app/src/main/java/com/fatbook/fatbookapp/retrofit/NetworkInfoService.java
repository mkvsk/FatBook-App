package com.fatbook.fatbookapp.retrofit;

import com.fatbook.fatbookapp.core.User;

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
    Call<Void> createNewUser(@Part MultipartBody.Part image);

//    @Part image: MultipartBody.Part,
//    @Part("desc") desc: RequestBody

    @GET("user/get-by-pid")
    Call<User> getUser(@Query(value = "pid") Long pid);

    @GET("user/get-by-login")
    Call<User> getUser(@Query(value = "login") String login);

    @GET("user/check-available-login")
    Call<Void> checkAvailableLogin(@Query(value = "login") String login);

}
