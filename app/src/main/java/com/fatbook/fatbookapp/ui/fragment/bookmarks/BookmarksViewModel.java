package com.fatbook.fatbookapp.ui.fragment.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class BookmarksViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Object>> mMap;

    public BookmarksViewModel(){
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
