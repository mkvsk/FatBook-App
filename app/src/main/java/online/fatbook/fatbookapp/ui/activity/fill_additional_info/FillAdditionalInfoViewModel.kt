package online.fatbook.fatbookapp.ui.activity.fill_additional_info

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.ui.activity.MainActivity
import java.io.File

class FillAdditionalInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val mMap: MutableLiveData<Map<String, Any>> = MutableLiveData()

    private fun fillData(mMap: MutableLiveData<Map<String, Any>>) {
        //TODO fill data for test
    }

    val map: LiveData<Map<String, Any>>
        get() = mMap

    fun saveUser(view: View?, user: User?, image: File?) {
        val intent = Intent(getApplication(), MainActivity::class.java)
        //        if (UserUtils.createNewUser(user, image)) {
//            intent.putExtra(UserUtils.TAG_USER, user);
//            view.getContext().startActivity(intent);
//        } else {
//            Toast.makeText(getApplication(), R.string.ERROR_CREATING_USER, Toast.LENGTH_SHORT).show();
//        }
    }

    init {
        fillData(mMap)
    }
}