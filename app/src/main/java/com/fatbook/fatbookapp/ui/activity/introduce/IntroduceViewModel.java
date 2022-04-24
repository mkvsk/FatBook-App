package com.fatbook.fatbookapp.ui.activity.introduce;

import android.app.Application;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.retrofit.RetrofitUtil;
import com.fatbook.fatbookapp.retrofit.RetrofitAPI;
import com.fatbook.fatbookapp.ui.activity.skip_additional_info.SkipAdditionalInfoActivity;
import com.fatbook.fatbookapp.util.UserUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class IntroduceViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<String, Object>> mMap;

    private boolean resultCheckAvailableLogin;

    public IntroduceViewModel(@NonNull Application application) {
        super(application);
        mMap = new MutableLiveData<>();
        fillData(mMap);
    }

    private void fillData(MutableLiveData<Map<String, Object>> mMap) {
        //TODO fill data for test
    }

    public LiveData<Map<String, Object>> getMap() {
        return mMap;
    }

    public boolean isLoginAvailable(String login) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitUtil.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofit.create(RetrofitAPI.class).checkAvailableLogin(login).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Void body = response.body();
                    System.out.println();
                }

                @Override
                public void onFailure(@EverythingIsNonNull Call<Void> call, Throwable t) {
                    resultCheckAvailableLogin = false;
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    public void goToAdditionalInfo(View view, String login) {
        Intent intent = new Intent(getApplication(), SkipAdditionalInfoActivity.class);
        User user = new User();
        user.setLogin(login);
        user.setRole(Role.USER);
//        user.setRegDate(new Date());
        intent.putExtra(UserUtil.USER, user);
        view.getContext().startActivity(intent);
    }
}
