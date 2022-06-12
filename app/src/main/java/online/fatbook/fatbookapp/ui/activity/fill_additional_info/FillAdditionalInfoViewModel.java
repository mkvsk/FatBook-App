package online.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fatbook.fatbookapp.R;

import java.io.File;
import java.util.Map;

import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.ui.activity.MainActivity;
import online.fatbook.fatbookapp.util.UserUtils;

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
        if (UserUtils.createNewUser(user, image)) {
            intent.putExtra(UserUtils.TAG_USER, user);
            view.getContext().startActivity(intent);
        } else {
            Toast.makeText(getApplication(), R.string.ERROR_CREATING_USER, Toast.LENGTH_SHORT).show();
        }
    }
}