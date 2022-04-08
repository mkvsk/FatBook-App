package com.fatbook.fatbookapp.ui.activity.introduce;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.databinding.ActivityIntroduceBinding;

public class IntroduceActivity extends AppCompatActivity {

    private ActivityIntroduceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroduceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntroduceViewModel viewModel = new ViewModelProvider(this).get(IntroduceViewModel.class);

    }
}
