package com.fatbook.fatbookapp.ui.activity.introduce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
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

        binding.editTextIntroduceUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        binding.scrollViewIntroduce.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            int heightDiff = binding.scrollViewIntroduce.getRootView().getHeight() - binding.scrollViewIntroduce.getHeight();
//
//            if (heightDiff > 100) { // Value should be less than keyboard's height
//                binding.scrollViewIntroduce.smoothScrollBy(0, binding.textViewIntroduceCopyright.getScrollY());
//            } else {
//                System.out.println();
//            }
//        });
    }
}
