package com.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.ActivityPasswordBinding;
import com.fatbook.fatbookapp.util.UserUtils;

public class PasswordActivity extends AppCompatActivity {

    private ActivityPasswordBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);

        //TODO remove stub
        toggleViews(true);
//        binding.buttonPasswordNext.setEnabled(false);
//        binding.buttonPasswordNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_blue_grey_200));

        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toggleViews(isValid(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.buttonPasswordNext.setOnClickListener(view -> {
            if (isValid(binding.editTextPassword.getText().toString())) {
                //TODO api call for save a user
                Intent intent = new Intent(this, SkipAdditionalInfoActivity.class);
                intent.putExtra(UserUtils.TAG_USER, user);
                startActivity(intent);
            } else {
                toggleViews(false);
            }
        });
    }

    private void toggleViews(boolean enable) {
        if (enable) {
            binding.imageViewPasswordIconAccepted.setVisibility(View.VISIBLE);
            binding.editTextPassword.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
            binding.buttonPasswordNext.setEnabled(true);
            binding.buttonPasswordNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_pink_a200));

        } else {
            binding.imageViewPasswordIconAccepted.setVisibility(View.INVISIBLE);
            binding.editTextPassword.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login_error));
            binding.buttonPasswordNext.setEnabled(false);
            binding.buttonPasswordNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_blue_grey_200));
        }
    }

    /**
     * https://mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
     * <p>
     * Password must contain at least one digit [0-9].
     * Password must contain at least one lowercase Latin character [a-z].
     * Password must contain at least one uppercase Latin character [A-Z].
     * Password must contain at least one special character like ! @ # & ( ).
     * Password must contain a length of at least 8 characters and a maximum of 20 characters.
     */
    private boolean isValid(String fat) {
        //TODO remove stub
//        return fat.length() >= 6 && fat.length() <= 16;
        return true;
//        return fat.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
    }
}
