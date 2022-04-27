package com.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.app.Application;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.UserUtil;

import java.io.File;
import java.util.Map;

public class FillAdditionalInfoViewModel extends AndroidViewModel {

    private final MutableLiveData<Map<String, Object>> mMap;

    public FillAdditionalInfoViewModel(@NonNull Application application) {
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

    public void saveUser(View view, User user, File image) {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        //TODO save user with retrofit
        user = UserUtil.createNewUser(view, user, image);
//        intent.putExtra(UserUtil.USER, user);
//        view.getContext().startActivity(intent);
    }
}