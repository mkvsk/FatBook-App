package com.fatbook.fatbookapp.ui.activity.skip_additional_info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivitySkipAdditionalInfoBinding;
import com.fatbook.fatbookapp.util.UserUtils;

public class SkipAdditionalInfoActivity extends AppCompatActivity {

    private ActivitySkipAdditionalInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySkipAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SkipAdditionalInfoViewModel viewModel = new ViewModelProvider(this).get(SkipAdditionalInfoViewModel.class);

        User user = (User) getIntent().getSerializableExtra(UserUtils.USER);

        binding.buttonSkipAddSkip.setOnClickListener(view -> {
            viewModel.skipAddInfo(binding.getRoot(), user);
            finish();
        });

        binding.buttonSkipAddFill.setOnClickListener(view -> {
            viewModel.fillAddInfo(binding.getRoot(), user);
            finish();
        });
    }
}
