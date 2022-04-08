package com.fatbook.fatbookapp.ui.activity.fill_additional_info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;

public class FillAdditionalInfoActivity extends AppCompatActivity {

    private ActivityFillAdditionalInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFillAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FillAdditionalInfoViewModel viewModel = new ViewModelProvider(this).get(FillAdditionalInfoViewModel.class);

    }
}
