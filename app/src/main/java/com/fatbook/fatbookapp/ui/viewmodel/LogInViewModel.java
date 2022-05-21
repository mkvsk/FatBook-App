package com.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;

import java.util.List;

public class LogInViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<User>();

    private final MutableLiveData<Boolean> isFatValid = new MutableLiveData<Boolean>();

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setFatValid(Boolean valid) {
        this.isFatValid.setValue(valid);
    }

    public LiveData<Boolean> getFatValid() {
        return isFatValid;
    }

}
