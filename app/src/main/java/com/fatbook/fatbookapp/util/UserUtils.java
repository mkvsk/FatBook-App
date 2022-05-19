package com.fatbook.fatbookapp.util;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;

import java.io.File;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class UserUtils {

    public static final String FILL_ADDITIONAL_INFO = "fill_additional_info";

    public static final String TAG_USER = "user";

    private static String imageURL = "";

    @SneakyThrows
    public static boolean createNewUser(User user, File image) {
        user.setImage(uploadUserProfileImage(image));
        //TODO save user
        return true;
    }

    @SneakyThrows
    private static String uploadUserProfileImage(File image) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        image
                );

        MultipartBody.Part file = MultipartBody.Part.createFormData("file", image.getName(), requestFile);

        Call<String> newUser = RetrofitFactory.infoServiceClient().createNewUser(file);
        newUser.enqueue(new Callback<String>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<String> call, Response<String> response) {
                imageURL = response.body();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println();
            }
        });
        return imageURL;
    }

}
