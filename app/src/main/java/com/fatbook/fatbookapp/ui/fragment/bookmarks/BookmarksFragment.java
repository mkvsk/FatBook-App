package com.fatbook.fatbookapp.ui.fragment.bookmarks;

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
import com.fatbook.fatbookapp.databinding.FragmentBookmarksBinding;
import com.google.android.material.snackbar.Snackbar;

public class BookmarksFragment extends Fragment {

    private FragmentBookmarksBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        BookmarksViewModel viewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);

        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
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
