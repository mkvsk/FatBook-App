package com.fatbook.fatbookapp.ui.activity.introduce;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityIntroduceBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.activity.skip_additional_info.SkipAdditionalInfoActivity;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroduceActivity extends AppCompatActivity {

    private ActivityIntroduceBinding binding;

    private boolean checkLoginResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroduceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editTextIntroduceUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkLoginAvailable();
            }
        });

        binding.buttonIntroduceNext.setOnClickListener(view -> {
            if (checkLoginAvailable()) {
                navigateToSkipAdditionalInfo(binding.getRoot(), binding.editTextIntroduceUsername.getText().toString());
            }
        });
    }

    private boolean checkLoginAvailable() {
//        if (isLoginAvailable(binding.editTextIntroduceUsername.getText().toString())) {
//            binding.imageViewIntroduceIconAccepted.setVisibility(View.VISIBLE);
//            return true;
//        } else {
//            binding.imageViewIntroduceIconAccepted.setVisibility(View.GONE);
//            return false;
//        }
        return true;
    }

    private boolean isLoginAvailable(String login) {
        try {
            RetrofitFactory.infoServiceClient().checkAvailableLogin(login).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Void body = response.body();
                    System.out.println();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    checkLoginResult = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkLoginResult;
    }

    private void navigateToSkipAdditionalInfo(View view, String login) {
        Intent intent = new Intent(getApplication(), SkipAdditionalInfoActivity.class);
        User user = new User();
        user.setLogin(login);
        user.setRole(Role.USER);
//        user.setRegDate(new Date());
        intent.putExtra(UserUtils.USER, user);
        view.getContext().startActivity(intent);
        finish();
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
