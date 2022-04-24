package com.fatbook.fatbookapp.util;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.retrofit.RetrofitAPI;
import com.fatbook.fatbookapp.retrofit.RetrofitUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserUtil {

    public static final String USER = "user";

    public static User createNewUser(User user) {
        File file = new File(user.getPhoto());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("files[0]", file.getName(), requestFile);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitUtil.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(RetrofitAPI.class).createNewUser(user, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println();
            }
        });
        return user;
    }

}
