package com.fatbook.fatbookapp.ui.activity.skip_additional_info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.databinding.ActivityFillAdditionalInfoBinding;
import com.fatbook.fatbookapp.databinding.ActivitySkipAdditionalInfoBinding;
import com.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoViewModel;

public class SkipAdditionalInfoActivity extends AppCompatActivity {

    private ActivitySkipAdditionalInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySkipAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SkipAdditionalInfoViewModel viewModel = new ViewModelProvider(this).get(SkipAdditionalInfoViewModel.class);

    }
}
