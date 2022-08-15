package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_test.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Recipe
import online.fatbook.fatbookapp.core.User
import online.fatbook.fatbookapp.databinding.FragmentTestBinding
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener

class TestFragment : Fragment(), OnRecipeClickListener {

    private var binding: FragmentTestBinding? = null

    private var expanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageview_recipes_qqt_userprofile.setOnClickListener {
            focusOnRecipes()
        }

        nsv_userprofile.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (!expanded) {
                if (scrollY >= 1044) {
                    floating_button_up.visibility = View.VISIBLE
                }
                if (scrollY < 1044) {
                    floating_button_up.visibility = View.GONE
                }
            } else {
                if (scrollY >= 1200) {
                    floating_button_up.visibility = View.VISIBLE
                }
                if (scrollY < 1200) {
                    floating_button_up.visibility = View.GONE
                }
            }
        })

        floating_button_up.setOnClickListener {
            focusOnRecipes()
        }

        imageview_ic_expand.setOnClickListener {
            TransitionManager.go(Scene(cardview_userprofile), AutoTransition())
            if (!expanded) {
                textview_bio_userprofile.maxLines = Integer.MAX_VALUE
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_less
                    )
                )
                expanded = true
            } else {
                textview_bio_userprofile.maxLines = 3
                imageview_ic_expand.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_expand_more
                    )
                )
                expanded = false
            }
        }

        button_messages.setOnClickListener {
            swipe_refresh_user_profile.isRefreshing = false
        }

        val list1 = listOf(
            Recipe(name = "sobaka1", forks = 255),
            Recipe(name = "sobaka2", forks = 1477, author = "Neshik"),
            Recipe(
                name = "Text text text text text text text text",
                forks = 1234567,
                author = "Timofey"
            ),
            Recipe(name = "sobaka4"),
            Recipe(name = "sobaka5"),
            Recipe(name = "sobaka6"),
            Recipe(name = "sobaka7"),
            Recipe(name = "sobaka8", forks = 1339),
            Recipe(name = "sobaka9"),
            Recipe(name = "sobaka10")
        )
        val list2 = listOf(
            Recipe(name = "kot1"),
            Recipe(name = "kot2"),
            Recipe(name = "kot3"),
            Recipe(name = "kot4"),
            Recipe(name = "kot5"),
            Recipe(name = "kot6"),
            Recipe(name = "kot7"),
            Recipe(name = "kot8"),
            Recipe(name = "kot9")
        )

        val adapter = RecipeAdapter()
        adapter.setData(list1, User())
        adapter.setClickListener(this)
        rv_user_recipe.adapter = adapter

        tabLayout_userprofile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) {
                    adapter.setData(list1)
                } else {
                    adapter.setData(list2)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun focusOnRecipes() {
        nsv_userprofile.post {
            nsv_userprofile.scrollTo(
                0,
                cardview_userprofile.bottom
            )
        }
    }

    override fun onRecipeClick(position: Int) {
        Log.d("recipe click", position.toString())
    }

    override fun onBookmarksClick(recipe: Recipe?, bookmark: Boolean, position: Int) {
        Log.d("bookmark click", position.toString())
    }

    override fun onForkClicked(recipe: Recipe?, fork: Boolean, position: Int) {
        Log.d("fork click", position.toString())
    }

}
