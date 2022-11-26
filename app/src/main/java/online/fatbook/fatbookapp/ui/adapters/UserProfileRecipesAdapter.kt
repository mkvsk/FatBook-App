package online.fatbook.fatbookapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import online.fatbook.fatbookapp.ui.fragment.user.UserRecipesPageFragment
import online.fatbook.fatbookapp.ui.fragment.user.FavouritesRecipesPageFragment

class UserProfileRecipesAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private lateinit var userRecipesPageFragment: UserRecipesPageFragment
    private lateinit var favouritesRecipesPageFragment: FavouritesRecipesPageFragment

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        println("UserProfileRecipesAdapter")
        return when (position) {
            0 -> {
                userRecipesPageFragment = UserRecipesPageFragment()
                userRecipesPageFragment
            }
            else -> {
                favouritesRecipesPageFragment = FavouritesRecipesPageFragment()
                favouritesRecipesPageFragment
            }
        }
    }

    fun setRecipes() {
//        userRecipesPageFragment.setData()
    }

    fun setFavs() {
//        favouritesRecipesPageFragment.setData()
    }

}