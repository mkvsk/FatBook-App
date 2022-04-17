package com.fatbook.fatbookapp.ui.fragment.feed;

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
import androidx.viewpager2.widget.ViewPager2;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.databinding.FragmentFeedBinding;
import com.fatbook.fatbookapp.ui.fragment.RecipeAllFragment;
import com.fatbook.fatbookapp.ui.fragment.RecipeFavouritesFragment;
import com.fatbook.fatbookapp.ui.fragment.feed.adapter.ViewPager2Adapter;
import com.google.android.material.tabs.TabLayout;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FeedViewModel viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupMenu();

        binding.editTextSearchFeed.setVisibility(View.GONE);
        binding.editTextSearchFeed.setText("");
        binding.toolbarFeed.setTitle("Recipe");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2Adapter adapter = new ViewPager2Adapter(this);

        binding.tabLayoutFeed.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pagerFeed.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        adapter.addFragment(new RecipeAllFragment());
        adapter.addFragment(new RecipeFavouritesFragment());
        binding.pagerFeed.setAdapter(adapter);

        binding.pagerFeed.setUserInputEnabled(false);

        binding.pagerFeed.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.scrollViewFeed.fling(0);
                binding.scrollViewFeed.scrollTo(0, 0);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //если открыт all recipes - разрешать поиск
    //если открыт favourites - запрещать поиск (скрывать editText)

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feed_search:
                binding.editTextSearchFeed.setVisibility(View.VISIBLE);
                binding.toolbarFeed.setTitle("");
                return true;
            case R.id.menu_feed_search_close:
                binding.editTextSearchFeed.setVisibility(View.GONE);
                binding.editTextSearchFeed.setText("");
                binding.toolbarFeed.setTitle("Recipe");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupMenu() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbarFeed);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
