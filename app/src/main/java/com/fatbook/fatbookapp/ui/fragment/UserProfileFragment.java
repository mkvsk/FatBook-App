package com.fatbook.fatbookapp.ui.fragment;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentUserProfileBinding;
import com.fatbook.fatbookapp.ui.adapters.UserRecipeAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;

    private User user;

    private UserViewModel userViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupMenu();

//        user = new User(1339L, "Tatyana Mayakovskaya", "hewix", null,
//                "Gradle – является отличным выбором в качестве систем сборки проектов. Подтверждением тому является то, что его используют разработчики таких известных проектов, как Spring и Hibernate. " +
//                        "Выше были рассмотрены лишь самые базовые вещи. За ними скрыт миллион особенностей и возможностей, которые появляются 123456",
//                Role.ADMIN, "https://sun9-12.userapi.com/s/v1/if2/WbpjaiKfC5Qw7qBjuIiXw0uNl93GiubjztSTN6HyyPyHqIjnhG-663S75ZyBMpCVgooC4-q-t5f5QZhpPLyZWBTh.jpg?size=1280x1280&quality=95&type=album",
//                null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        user = userViewModel.getUser().getValue();

        fillUserProfile();
        editMode(false);

        List<Recipe> recipes = new ArrayList<>();
        getRecipeList(recipes);
        RecyclerView recyclerView = binding.rvUserRecipe;
        UserRecipeAdapter adapter = new UserRecipeAdapter(binding.getRoot().getContext(), recipes);
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
                Snackbar.make(binding.getRoot(), R.string.snackbar_edit, Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
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
                Snackbar.make(binding.getRoot(), R.string.snackbar_end_edit, Snackbar.LENGTH_SHORT)
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                cancelEdit();
                editMode(false);
                revertUserData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editMode(boolean allow) {
        binding.editTextProfileName.setEnabled(allow);
        binding.editTextProfileBio.setEnabled(allow);
        if (allow) {
            binding.buttonUserProfileAddPhoto.setVisibility(View.VISIBLE);
            binding.editTextProfileName.setBackgroundResource(R.drawable.edit_mode_bgr);
            binding.editTextProfileBio.setBackgroundResource(R.drawable.edit_mode_bgr);
        } else {
            binding.buttonUserProfileAddPhoto.setVisibility(View.GONE);
            binding.editTextProfileName.setBackgroundResource(R.drawable.round_corner_rect_white);
            binding.editTextProfileBio.setBackgroundResource(R.drawable.round_corner_rect_white);
        }

    }

    private void revertUserData() {
        fillUserProfile();
    }

    private void changeUserData() {
        user.setName(binding.editTextProfileName.getText().toString());
//        user.setBirthday(binding.textViewProfileBirthday.getText().toString());
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


    private void getRecipeList(List<Recipe> recipes) {
        recipes.add(new Recipe(1L, "PotatoChips", "qqqqq", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg", 1339, ""));
        recipes.add(new Recipe(2L, "Potato", "qqqqq", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----2.jpg", 21345, ""));
        recipes.add(new Recipe(13L, "fried PotatoChips", "qqqqq", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----1.jpg", 0, ""));
        recipes.add(new Recipe(11L, "creamy Potato", "sssss", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----3.jpg", 8, ""));
        recipes.add(new Recipe(1L, "Potatoes with kotletki", "asasasasasas", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----6.jpg", 133349, ""));
        recipes.add(new Recipe(1L, "Potato so smetanka", "kkkkk", user.getLogin(), Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----4.jpg", 324, ""));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
