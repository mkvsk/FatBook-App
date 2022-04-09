package com.fatbook.fatbookapp.ui.fragment.add_recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fatbook.fatbookapp.databinding.FragmentAddRecipeBinding;

public class AddRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getContext(), "add new fat recipe", Toast.LENGTH_LONG).show();

        AddRecipeViewModel viewModel = new ViewModelProvider(this).get(AddRecipeViewModel.class);

        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
