package com.fatbook.fatbookapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fatbook.fatbookapp.databinding.FragmentRecipesAllBinding;
import com.fatbook.fatbookapp.databinding.FragmentRecipesFavouritesBinding;

public class RecipeFavouritesFragment extends Fragment {

    private FragmentRecipesFavouritesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        AddRecipeViewModel viewModel = new ViewModelProvider(this).get(AddRecipeViewModel.class);

        binding = FragmentRecipesFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "FAVOURITES", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
