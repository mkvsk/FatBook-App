package com.fatbook.fatbookapp.ui.activity.introduce;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.databinding.ActivityIntroduceBinding;

public class IntroduceActivity extends AppCompatActivity {

    private ActivityIntroduceBinding binding;
    private IntroduceViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroduceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(IntroduceViewModel.class);

        binding.editTextIntroduceUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (viewModel.isLoginAvailable(binding.editTextIntroduceUsername.getText().toString())) {
                    binding.imageViewIntroduceIconAccepted.setVisibility(View.VISIBLE);
                } else {
                    binding.imageViewIntroduceIconAccepted.setVisibility(View.GONE);
                }
            }
        });

        binding.buttonIntroduceNext.setOnClickListener(view ->
                viewModel.goToAdditionalInfo(binding.getRoot(), binding.editTextIntroduceUsername.getText().toString())
        );
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
