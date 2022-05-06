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

import com.bumptech.glide.Glide;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentUserProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private User user;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        UserProfileViewModel viewModel =
                new ViewModelProvider(this).get(UserProfileViewModel.class);

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupMenu();

        user = new User(1339L, "Tatyana Mayakovskaya", "hewix", "15 June",
                "Gradle – является отличным выбором в качестве систем сборки проектов. Подтверждением тому является то, что его используют разработчики таких известных проектов, как Spring и Hibernate. " +
                        "Выше были рассмотрены лишь самые базовые вещи. За ними скрыт миллион особенностей и возможностей, которые появляются 123456",
                Role.ADMIN, "https://sun9-12.userapi.com/s/v1/if2/WbpjaiKfC5Qw7qBjuIiXw0uNl93GiubjztSTN6HyyPyHqIjnhG-663S75ZyBMpCVgooC4-q-t5f5QZhpPLyZWBTh.jpg?size=1280x1280&quality=95&type=album");

        fillUserProfile();
        editMode(false);

        return root;
    }

    private void fillUserProfile() {
        binding.toolbarUserProfile.setTitle(user.getLogin());
        binding.textViewProfileFullName.setText(user.getName());
        binding.textViewProfileBio.setText(user.getBio());
        String birthday = getString(R.string.text_birthday) + user.getBirthday();
        binding.textViewProfileBirthday.setText(birthday);
        Glide
                .with(getLayoutInflater().getContext())
                .load(user.getImage())
                .into(binding.imageViewProfilePhoto);

        Glide
                .with(getLayoutInflater().getContext())
                .load(user.getImage())
                .into(binding.imageViewUserProfilePhotoBgr);
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
                editMode(true);
                return true;
            case R.id.menu_user_profile_save:
                Snackbar.make(binding.getRoot(), "Saved", Snackbar.LENGTH_SHORT)
                        .setAnchorView(getActivity().findViewById(R.id.nav_view)).show();
                confirmEdit();
                editMode(false);
                changeUserData();
                return true;
            case R.id.menu_user_profile_cancel:
                Snackbar.make(binding.getRoot(), "End edit", Snackbar.LENGTH_SHORT)
                        .setAnchorView(getActivity().findViewById(R.id.nav_view)).show();
                cancelEdit();
                editMode(false);
                revertUserData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editMode(boolean allow) {
        binding.textViewProfileFullName.setEnabled(allow);
        binding.textViewProfileBirthday.setEnabled(allow);
        binding.textViewProfileBio.setEnabled(allow);
    }

    private void revertUserData() {
       fillUserProfile();
    }

    private void changeUserData() {
        user.setName(binding.textViewProfileFullName.getText().toString());
        user.setBirthday(binding.textViewProfileBirthday.getText().toString());
        user.setBio(binding.textViewProfileBio.getText().toString());
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
