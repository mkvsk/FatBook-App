package com.fatbook.fatbookapp.ui.activity.skip_additional_info;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.MainActivity;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivitySkipAdditionalInfoBinding;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

public class SkipAdditionalInfoActivity extends AppCompatActivity {

    private ActivitySkipAdditionalInfoBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySkipAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra(UserUtils.USER);

        binding.buttonSkip.setOnClickListener(view -> {
            navigateToMainActivity(false);
        });

        binding.buttonFill.setOnClickListener(view -> {
           navigateToMainActivity(true);
        });
    }

    private void navigateToMainActivity(boolean fill) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(UserUtils.USER, user);
        intent.putExtra(UserUtils.FILL_ADDITIONAL_INFO, fill);
        startActivity(intent);
        finish();
    }
}
