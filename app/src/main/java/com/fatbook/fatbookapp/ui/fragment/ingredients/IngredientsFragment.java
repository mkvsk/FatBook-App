package com.fatbook.fatbookapp.ui.fragment.ingredients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import com.fatbook.fatbookapp.databinding.FragmentIngredientsBinding;
import com.fatbook.fatbookapp.ui.fragment.bookmarks.BookmarksViewModel;
import com.google.android.material.snackbar.Snackbar;

public class IngredientsFragment extends Fragment {

    private FragmentIngredientsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        IngredientsViewModel viewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);

        binding = FragmentIngredientsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabIngredientsAdd.setOnClickListener(view1 -> {
            Toast.makeText(getContext(), "Add new ingredient", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
