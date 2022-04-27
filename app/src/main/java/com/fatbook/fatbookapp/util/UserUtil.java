package com.fatbook.fatbookapp.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUtil {

    public static final String USER = "user";

    @SneakyThrows
    public static User createNewUser(View view, User user, File image) {
//        File file = new File(user.getUri().getPath());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        image
                );
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), user.getUri());
//            System.out.println();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody user1 = RequestBody.create(MediaType.parse("text/plain"), user.getLogin());
//        RequestBody file1 = RequestBody.create(MediaType.parse("image/jpeg"), image);
//
//        ContextWrapper contextWrapper = new ContextWrapper(view.getContext());
//        File images = contextWrapper.getDir("Images", Context.MODE_PRIVATE);
//        images = new File(images, "asdfasdf.jpg");
//        InputStream in = new FileInputStream(image);
//        byte[] buf;
//        buf = new byte[in.available()];
//        while (in.read(buf) != -1) ;
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buf);
//        Call<Void> voidCall = RetrofitFactory.infoServiceClient().uploadPic(requestBody);

        MultipartBody.Part file = MultipartBody.Part.createFormData("file", image.getName(), requestFile);

        Call<Void> newUser = RetrofitFactory.infoServiceClient().createNewUser(file);
        newUser.enqueue(new Callback<Void>() {
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
