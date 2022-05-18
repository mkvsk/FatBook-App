package com.fatbook.fatbookapp.ui.fragment;

import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import com.fatbook.fatbookapp.ui.adapters.BookmarkAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarksFragment extends Fragment {

    private FragmentBookmarksBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        binding.swipeRefreshBookmarks.setColorSchemeColors(
                getResources().getColor(R.color.color_pink_a200));
        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            Toast.makeText(binding.getRoot().getContext(), "refreshed", Toast.LENGTH_SHORT).show();
            binding.swipeRefreshBookmarks.setRefreshing(false);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Recipe> recipes = new ArrayList<>();

        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);
        getRecipeList(recipes);

        RecyclerView recyclerView = binding.rvBookmarks;

        BookmarkAdapter adapter = new BookmarkAdapter(binding.getRoot().getContext(), recipes);

        recyclerView.setAdapter(adapter);
    }

    private void getRecipeList(List<Recipe> recipes) {
        User user = new User(1L, "qwe", "Kuzya", null, "Kuzya the cat",
                Role.USER, "https://media.2x2tv.ru/content/images/size/h1080/2021/05/-----5.jpg",
                null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        recipes.add(new Recipe(1L, "PotatoChips", "qqqqq", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/db/62/c6/db62c6b63a86c17cc6f95389c99ea2e7.jpg", 1339));
        recipes.add(new Recipe(2L, "Potato", "qqqqq", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/e9/87/ad/e987ad4e1e51ff6cc412e6a57b599c65.jpg", 21345));
        recipes.add(new Recipe(13L, "fried PotatoChips", "qqqqq", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/c6/25/e0/c625e0dccb378a48e7a668de657825cd.jpg", 0));
        recipes.add(new Recipe(11L, "Potato", "sssss", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/e1/96/03/e19603b186c8923018790f53b52ad382.jpg", 8));
        recipes.add(new Recipe(1L, "Potatoes with kotletki", "asasasasasas", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/68/52/f6/6852f6a72a8f0d2a592f1095a8c6089d.jpg", 133349));
        recipes.add(new Recipe(1L, "Potato so smetanka", "kkkkk", user, Collections.emptyList(),
                "https://i.pinimg.com/564x/a3/73/35/a37335511a8e857f03c86a9aab5a8ee7.jpg", 324));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
