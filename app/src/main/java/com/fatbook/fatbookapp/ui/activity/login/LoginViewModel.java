package com.fatbook.fatbookapp.ui.activity.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Object>> mMap;

    public LoginViewModel() {
        mMap = new MutableLiveData<>();
        fillData(mMap);
    }

    private void fillData(MutableLiveData<Map<String, Object>> mMap) {
        //TODO fill data for test
    }

    public LiveData<Map<String, Object>> getMap() {
        return mMap;
    }
}
