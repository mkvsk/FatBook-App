package online.fatbook.fatbookapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    var binding: ActivityAuthenticationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}