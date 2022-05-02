package com.fatbook.fatbookapp.ui.fragment.feed;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.ui.adapters.RecipeAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FeedViewModel viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Recipe> recipes = new ArrayList<>();

        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);

        RecyclerView recyclerView = binding.rvFeed;

        RecipeAdapter adapter = new RecipeAdapter(binding.getRoot().getContext(), recipes);

        recyclerView.setAdapter(adapter);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((view1, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                getActivity().finish();
            }
            return false;
        });
    }

    private void getRecipeList(List<Recipe> recipes) {
        User user = new User(1L, "qwe", "Moonya", "null", "Moonya the cat",
                Role.USER, "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg");

        recipes.add(new Recipe(1L, "PotatoChips", "qqqqq", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg", 1339));
        recipes.add(new Recipe(2L, "Potato", "qqqqq", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----2.jpg", 21345));
        recipes.add(new Recipe(13L, "fried PotatoChips", "qqqqq", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----1.jpg", 0));
        recipes.add(new Recipe(11L, "creamy Potato", "sssss", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----3.jpg", 8));
        recipes.add(new Recipe(1L, "Potatoes with kotletki", "asasasasasas", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----6.jpg", 133349));
        recipes.add(new Recipe(1L, "Potato so smetanka", "kkkkk", user, Collections.emptyList(),
                "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----4.jpg", 324));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
