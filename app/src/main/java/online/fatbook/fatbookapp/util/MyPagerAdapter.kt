package online.fatbook.fatbookapp.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import online.fatbook.fatbookapp.ui.fragment.user.AllRecipesPageFragment
import online.fatbook.fatbookapp.ui.fragment.user.FavouritesRecipesPageFragment

class MyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllRecipesPageFragment()
            }
            else -> {
                FavouritesRecipesPageFragment()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun containsItem(itemId: Long): Boolean {
        return super.containsItem(itemId)
    }
}