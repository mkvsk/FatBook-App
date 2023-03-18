package online.fatbook.fatbookapp.ui.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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