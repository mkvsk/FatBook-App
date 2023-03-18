package online.fatbook.fatbookapp.ui.user.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.ui.user.ui.main.FavouritesRecipesPageFragment
import online.fatbook.fatbookapp.ui.user.ui.main.UserRecipesPageFragment

class UserProfileRecipesAdapter(fragment: Fragment, val user: User) : FragmentStateAdapter(fragment) {

    private var userRecipesPageFragment: UserRecipesPageFragment = UserRecipesPageFragment(user)
    private var favouritesRecipesPageFragment: FavouritesRecipesPageFragment = FavouritesRecipesPageFragment(user)

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                userRecipesPageFragment
            }
            else -> {
                favouritesRecipesPageFragment
            }
        }
    }

    fun setData(user: User) {
        userRecipesPageFragment.setData(user)
        favouritesRecipesPageFragment.setData(user)
    }
}