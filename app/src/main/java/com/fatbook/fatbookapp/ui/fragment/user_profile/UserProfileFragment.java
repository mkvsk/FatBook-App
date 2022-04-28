package com.fatbook.fatbookapp.ui.fragment.user_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.databinding.FragmentUserProfileBinding;
import com.google.android.material.snackbar.Snackbar;

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
