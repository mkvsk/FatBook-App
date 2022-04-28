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

public class UserUtils {

    public static final String USER = "user";

    private static String imageURL = "";

    @SneakyThrows
    public static boolean createNewUser(User user, File image) {
        user.setImage(uploadUserProfileImage(image));
        //TODO save user
        return true;
    }

    private static String uploadUserProfileImage(File image) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        image
                );

        MultipartBody.Part file = MultipartBody.Part.createFormData("file", image.getName(), requestFile);

        Call<Void> newUser = RetrofitFactory.infoServiceClient().createNewUser(file);
        newUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                imageURL = "fatbook.online/bla.jpg";
                System.out.println();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println();
            }
        });
        return imageURL;
    }

}
