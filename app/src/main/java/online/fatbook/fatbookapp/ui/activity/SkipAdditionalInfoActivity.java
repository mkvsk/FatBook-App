package online.fatbook.fatbookapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;
import online.fatbook.fatbookapp.util.UserUtils;

import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class SkipAdditionalInfoActivity extends AppCompatActivity {

    private ActivitySkipAdditionalInfoBinding binding;

    private User user;

    private String fat;

    private boolean fill = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySkipAdditionalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra(UserUtils.TAG_USER);
        fat = getIntent().getStringExtra(UserUtils.TAG_FAT);

        String dialog = getResources().getString(R.string.cat_dialog_skip_add);
        binding.textViewSkipAddCatDialog.setText(String.format(dialog, user.getLogin()));

        binding.buttonSkip.setOnClickListener(view -> {
            fill = false;
            saveUser();
        });

        binding.buttonFill.setOnClickListener(view -> {
            fill = true;
            saveUser();
        });
    }

    private void saveUser() {
        RetrofitFactory.apiServiceClient().userCreate(user, fat).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "save user code " + response.code());
                if (response.code() == 200) {
                    user = response.body();
                    navigateToMainActivity(fill);
                } else {
                    showErrorMsg();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "save user code FAILED");
                showErrorMsg();
            }
        });
    }

    private void showErrorMsg() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }


    private void navigateToMainActivity(boolean navigate) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(UserUtils.TAG_USER, user);
        intent.putExtra(UserUtils.FILL_ADDITIONAL_INFO, navigate);
        startActivity(intent);
        finishAffinity();
    }
}
