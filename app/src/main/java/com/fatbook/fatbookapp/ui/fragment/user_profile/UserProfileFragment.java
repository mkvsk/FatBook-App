package com.fatbook.fatbookapp.ui.fragment.user_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentUserProfileBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.retrofit.NetworkInfoService;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        UserProfileViewModel viewModel =
                new ViewModelProvider(this).get(UserProfileViewModel.class);

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupMenu();
        return root;
    }

    private void setupMenu() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbarUserProfile);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_user_profile_edit:
                Snackbar.make(binding.getRoot(), "Edit", Snackbar.LENGTH_SHORT)
                        .setAnchorView(getActivity().findViewById(R.id.nav_view)).show();
                changeMenuItemsVisibility(false, true, true);
                return true;
            case R.id.menu_user_profile_save:
                Snackbar.make(binding.getRoot(), "Saved", Snackbar.LENGTH_SHORT)
                        .setAnchorView(getActivity().findViewById(R.id.nav_view)).show();
                confirmEdit();
                return true;
            case R.id.menu_user_profile_cancel:
                Snackbar.make(binding.getRoot(), "End edit", Snackbar.LENGTH_SHORT)
                        .setAnchorView(getActivity().findViewById(R.id.nav_view)).show();
                cancelEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmEdit() {
        changeMenuItemsVisibility(true, false, false);
        saveUser();
    }

    private void saveUser() {
        //TODO save user profile changes
    }

    private void cancelEdit() {
        changeMenuItemsVisibility(true, false, false);
        revertChanges();
    }

    private void revertChanges() {
        //TODO revert user profile changes
    }

    private void changeMenuItemsVisibility(boolean edit, boolean save, boolean cancel) {
        binding.toolbarUserProfile.getMenu().findItem(R.id.menu_user_profile_edit).setVisible(edit);
        binding.toolbarUserProfile.getMenu().findItem(R.id.menu_user_profile_save).setVisible(save);
        binding.toolbarUserProfile.getMenu().findItem(R.id.menu_user_profile_cancel).setVisible(cancel);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.btnUpload.setOnClickListener(view1 -> {
//            User user = new User(null, "Moonya", "moonya", null, "privet, ya Moonya", null,
//                    Role.ADMIN, "", null);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(RetrofitFactory.URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
////           save(user, retrofit.create(RetrofitAPI.class));
//            get(162, retrofit.create(NetworkInfoService.class));
//        });
    }

    private void get(long pid, NetworkInfoService api) {
        api.getUser(pid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(getContext(), response.body().getLogin(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save(User user, NetworkInfoService api) {
//        api.createNewUser(user, null).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
