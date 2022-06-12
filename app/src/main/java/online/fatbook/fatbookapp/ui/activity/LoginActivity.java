package online.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import online.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.Role;
import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.databinding.ActivityLoginBinding;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel;
import online.fatbook.fatbookapp.util.UserUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private SignInViewModel signInViewModel;

    private boolean btnNextClicked = false;

    private boolean isKeyboardVisible = false;

    public final ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rectangle = new Rect();
            final View contentView = binding.getRoot();
            contentView.getWindowVisibleDisplayFrame(rectangle);
            int screenHeight = contentView.getRootView().getHeight();
            int keypadHeight = screenHeight - rectangle.bottom;
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (isKeyboardVisible != isKeyboardNowVisible) {
                if (isKeyboardNowVisible) {
                    binding.textViewLoginVersion.setVisibility(View.GONE);
                    binding.textViewLoginCopyright.setVisibility(View.GONE);
                    binding.textViewLoginTagline.setVisibility(View.GONE);
                } else {
                    binding.textViewLoginVersion.setVisibility(View.VISIBLE);
                    binding.textViewLoginCopyright.setVisibility(View.VISIBLE);
                    binding.textViewLoginTagline.setVisibility(View.VISIBLE);
                }
            }
            isKeyboardVisible = isKeyboardNowVisible;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLoginNext.setEnabled(false);
        binding.buttonLoginNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_blue_grey_200));

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        signInViewModel.getLoginAvailable().observe(this, available -> {
            if (available) {
                toggleViews(true);
                if (btnNextClicked) {
                    Intent intent = new Intent(getApplication(), PasswordActivity.class);
                    User user = new User();
                    user.setLogin(binding.editTextLogin.getText().toString());
                    user.setRole(Role.USER);
                    user.setBirthday(StringUtils.EMPTY);
                    user.setRecipes(new ArrayList<>());
                    user.setRecipesForked(new ArrayList<>());
                    user.setRecipesBookmarked(new ArrayList<>());
                    user.setRegDate(UserUtils.regDateFormat.format(new Date()));
                    intent.putExtra(UserUtils.TAG_USER, user);
                    startActivity(intent);
                    btnNextClicked = false;
                }
            } else {
                toggleViews(false);
                btnNextClicked = false;
            }
        });

        binding.editTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateLogin(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.buttonLoginNext.setOnClickListener(view -> {
            btnNextClicked = true;
            validateLogin(binding.editTextLogin.getText().toString());
        });
    }

    private void toggleViews(boolean enable) {
        if (enable) {
            binding.imageViewLoginIconAccepted.setVisibility(View.VISIBLE);
            binding.editTextLogin.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login));
            binding.buttonLoginNext.setEnabled(true);
            binding.buttonLoginNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_pink_a200));

        } else {
            binding.imageViewLoginIconAccepted.setVisibility(View.INVISIBLE);
            binding.editTextLogin.setBackground(AppCompatResources.getDrawable(getBaseContext(), R.drawable.round_corner_edittext_login_error));
            binding.buttonLoginNext.setEnabled(false);
            binding.buttonLoginNext.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_blue_grey_200));
        }
    }

    private void validateLogin(String login) {
        if (login.matches("[a-zA-Z0-9]+") && login.length() >= 4) {
            log.log(Level.INFO, "login valid");
            loginCheckForCreation(login);
        } else {
            log.log(Level.INFO, "login invalid");
            toggleViews(false);
        }
    }

    private void loginCheckForCreation(String login) {
        RetrofitFactory.apiServiceClient().loginCheck(login).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                log.log(Level.INFO, "login check SUCCESS: " + response.code() + " = " + response.body());
                if (response.code() == 200) {
                    signInViewModel.setLoginAvailable(response.body());
                } else {
                    signInViewModel.setLoginAvailable(false);
                }

                if (response.body()) {
                    log.log(Level.INFO, "login available");
                } else {
                    log.log(Level.INFO, "login unavailable");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                log.log(Level.INFO, "login check FAILED");
                t.printStackTrace();
                signInViewModel.setLoginAvailable(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
}
