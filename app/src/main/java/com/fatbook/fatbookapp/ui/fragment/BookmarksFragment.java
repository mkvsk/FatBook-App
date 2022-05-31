package com.fatbook.fatbookapp.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.telephony.RadioAccessSpecifier;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.Role;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import com.fatbook.fatbookapp.ui.adapters.BookmarkAdapter;
import com.fatbook.fatbookapp.ui.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarksFragment extends Fragment {

    private FragmentBookmarksBinding binding;

    private UserViewModel userViewModel;

    private List<Recipe> recipeList;

    private BookmarkAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swipeRefreshBookmarks.setColorSchemeColors(getResources().getColor(R.color.color_pink_a200));
        binding.swipeRefreshBookmarks.setOnRefreshListener(() -> {
            Toast.makeText(binding.getRoot().getContext(), R.string.toast_refreshed, Toast.LENGTH_SHORT).show();
            binding.swipeRefreshBookmarks.setRefreshing(false);
        });

        recipeList = new ArrayList<>();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUser().observe(getViewLifecycleOwner(), _user -> {
            recipeList = _user.getRecipesBookmarked();
            RecyclerView recyclerView = binding.rvBookmarks;
            adapter = new BookmarkAdapter(binding.getRoot().getContext(), recipeList);
            recyclerView.setAdapter(adapter);
        });
    }

    private void getRecipeList(List<Recipe> recipes) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
