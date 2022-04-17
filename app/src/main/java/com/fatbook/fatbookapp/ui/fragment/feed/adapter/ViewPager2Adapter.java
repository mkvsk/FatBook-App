package com.fatbook.fatbookapp.ui.fragment.feed.adapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private final List<Fragment> listOfFragments = new ArrayList<>();

    public ViewPager2Adapter(Fragment fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment) {
        listOfFragments.add(fragment);
        notifyItemInserted(listOfFragments.size());
    }

    @Override
    public Fragment createFragment(int position) {
        return listOfFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return listOfFragments.size();
    }
}
