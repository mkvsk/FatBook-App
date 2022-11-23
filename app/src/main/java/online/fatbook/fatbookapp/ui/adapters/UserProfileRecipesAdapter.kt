package online.fatbook.fatbookapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import online.fatbook.fatbookapp.ui.fragment.user.UserRecipesPageFragment
import online.fatbook.fatbookapp.ui.fragment.user.FavouritesRecipesPageFragment

class UserProfileRecipesAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UserRecipesPageFragment()
            }
            else -> {
                FavouritesRecipesPageFragment()
            }
        }
    }

}