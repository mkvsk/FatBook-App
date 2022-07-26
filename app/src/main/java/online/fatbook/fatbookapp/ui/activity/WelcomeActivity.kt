package online.fatbook.fatbookapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import lombok.extern.java.Log
import online.fatbook.fatbookapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private var binding: ActivityWelcomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.buttonRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }
        binding!!.buttonSignIn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignInActivity::class.java
                )
            )
        }
    }

}