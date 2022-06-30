package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.ActivitySkipAdditionalInfoBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.util.UserUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkipAdditionalInfoActivity : AppCompatActivity() {
    private var binding: ActivitySkipAdditionalInfoBinding? = null
    private var user: User? = null
    private var fat: String? = null
    private var fill = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkipAdditionalInfoBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        user = intent.getSerializableExtra(UserUtils.TAG_USER) as User?
        fat = intent.getStringExtra(UserUtils.TAG_FAT)
        val dialog = resources.getString(R.string.cat_dialog_skip_add)
        binding!!.textViewSkipAddCatDialog.text = String.format(dialog, user!!.login)
        binding!!.buttonSkip.setOnClickListener { view: View? ->
            fill = false
            saveUser()
        }
        binding!!.buttonFill.setOnClickListener { view: View? ->
            fill = true
            saveUser()
        }
    }

    private fun saveUser() {
        RetrofitFactory.apiServiceClient().userCreate(user, fat).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
//                SkipAdditionalInfoActivity.log.log(Level.INFO, "save user code " + response.code())
                if (response.code() == 200) {
                    user = response.body()
                    navigateToMainActivity(fill)
                } else {
                    showErrorMsg()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
//                SkipAdditionalInfoActivity.log.log(Level.INFO, "save user code FAILED")
                showErrorMsg()
            }
        })
    }

    private fun showErrorMsg() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity(navigate: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(UserUtils.TAG_USER, user)
        intent.putExtra(UserUtils.FILL_ADDITIONAL_INFO, navigate)
        startActivity(intent)
        finishAffinity()
    }
}