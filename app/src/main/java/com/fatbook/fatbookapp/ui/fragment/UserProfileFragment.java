package com.fatbook.fatbookapp.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentUserProfileBinding;
import com.fatbook.fatbookapp.retrofit.RetrofitFactory;
import com.fatbook.fatbookapp.ui.activity.SplashActivity;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.fatbook.fatbookapp.util.UserUtils;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;

import lombok.extern.java.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class UserProfileFragment extends Fragment implements OnRecipeClickListener {

    private FragmentUserProfileBinding binding;

    private User user;

    private UserViewModel userViewModel;

    private RecipeAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        setupMenu();

        user = userViewModel.getUser().getValue();
        loadUserData(user.getPid());
        userViewModel.getUser().observe(getViewLifecycleOwner(), _user -> {
            user = _user;
            fillUserProfile();
        });
        setupAdapter();

        editMode(false);
        binding.toolbarUserProfile.getOverflowIcon().setColorFilter(getResources().getColor(R.color.color_blue_grey_600), PorterDuff.Mode.MULTIPLY);
    }

    private void loadUserData(long userPid) {
        RetrofitFactory.apiServiceClient().getUser(userPid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                log.log(Level.INFO, "" + response.code() + " found user: " + response.body());
                userViewModel.setUser(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                log.log(Level.INFO, "load user ERROR");
            }
        });
    }

    private void setupAdapter() {
        RecyclerView recyclerView = binding.rvUserRecipe;
        adapter = new RecipeAdapter(binding.getRoot().getContext(), user.getRecipes(), user, true, this);
        recyclerView.setAdapter(adapter);
    }

    private void fillUserProfile() {
        binding.toolbarUserProfile.setTitle(user.getLogin());
        if (StringUtils.isNotEmpty(user.getName())) {
            binding.editTextProfileName.setText(user.getName());
        }
        if (StringUtils.isNotEmpty(user.getBio())) {
            binding.editTextProfileBio.setText(user.getBio());
        }
        if (StringUtils.isNotEmpty(user.getImage())) {
            Glide
                    .with(getLayoutInflater().getContext())
                    .load(user.getImage())
                    .into(binding.imageViewProfilePhoto);
            Glide
                    .with(getLayoutInflater().getContext())
                    .load(user.getImage())
                    .into(binding.imageViewUserProfilePhotoBgr);
        }
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
                changeMenuItemsVisibility(false, true, true);
                editMode(true);
                return true;
            case R.id.menu_user_profile_save:
                Snackbar.make(binding.getRoot(), R.string.snackbar_saved, Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                confirmEdit();
                editMode(false);
                changeUserData();
                return true;
            case R.id.menu_user_profile_cancel:
                cancelEdit();
                editMode(false);
                revertUserData();
                return true;
            case R.id.menu_user_profile_app_info:
                Snackbar.make(binding.getRoot(), "TODO app info", Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                return true;
            case R.id.menu_user_profile_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        String msg = getResources().getString(R.string.alert_dialog_logout_message);

        final TextView textViewMsg = new TextView(requireContext());
        textViewMsg.setText(msg);
        textViewMsg.setSingleLine();
        textViewMsg.setTextColor(getResources().getColor(R.color.color_blue_grey_600));

        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        textViewMsg.setLayoutParams(params);
        container.addView(textViewMsg);

        View title = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_title_logout, null);
        new AlertDialog.Builder(requireContext())
                .setView(container)
                .setCustomTitle(title)
                .setPositiveButton(getString(R.string.alert_dialog_btn_yes), (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(UserUtils.APP_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(UserUtils.USER_PID, 0L);
                    editor.apply();
                    startActivity(new Intent(requireActivity(), SplashActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton(getString(R.string.alert_dialog_btn_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void editMode(boolean allow) {
        binding.editTextProfileName.setEnabled(allow);
        binding.editTextProfileBio.setEnabled(allow);
        if (allow) {
//            binding.buttonUserProfileAddPhoto.setVisibility(View.VISIBLE);
            binding.editTextProfileName.setBackgroundResource(R.drawable.edit_mode_bgr);
            binding.editTextProfileBio.setBackgroundResource(R.drawable.edit_mode_bgr);
        } else {
//            binding.buttonUserProfileAddPhoto.setVisibility(View.GONE);
            binding.editTextProfileName.setBackgroundResource(R.drawable.round_corner_rect_white);
            binding.editTextProfileBio.setBackgroundResource(R.drawable.round_corner_rect_white);
        }
    }

    private void revertUserData() {
        fillUserProfile();
    }

    private void changeUserData() {
        user.setName(binding.editTextProfileName.getText().toString());
        user.setBio(binding.editTextProfileBio.getText().toString());
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
    public void onRecipeClick(int position) {
        System.out.println("Stub!");
    }

    @Override
    public void onBookmarksClick(Recipe recipe, boolean add) {
        System.out.println("Stub!");
    }

    @Override
    public void onForkClicked(Recipe recipe, boolean fork) {
        System.out.println("Stub!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
