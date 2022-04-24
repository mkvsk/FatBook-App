package com.fatbook.fatbookapp.ui.activity.skip_additional_info;

import android.app.Application;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoActivity;
import com.fatbook.fatbookapp.util.UserUtil;

import java.util.Map;

public class SkipAdditionalInfoViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<String, Object>> mMap;

    public SkipAdditionalInfoViewModel(@NonNull Application application) {
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

    public void fillAddInfo(View view, User user) {
        Intent intent = new Intent(getApplication(), FillAdditionalInfoActivity.class);
        intent.putExtra(UserUtil.USER, user);
        view.getContext().startActivity(intent);
    }

    public void skipAddInfo(View view, User user) {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        //TODO retrofit save user
        view.getContext().startActivity(intent);
    }
}
