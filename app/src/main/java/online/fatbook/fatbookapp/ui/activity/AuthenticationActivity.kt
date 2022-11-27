package online.fatbook.fatbookapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    var _binding: ActivityAuthenticationBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}