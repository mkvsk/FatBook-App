package online.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import lombok.extern.java.Log;

@Log
public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRegister.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        binding.buttonSignIn.setOnClickListener(view -> startActivity(new Intent(this, SignInActivity.class)));
    }
}
