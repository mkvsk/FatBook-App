package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_add_new_recipe_ingredients_old.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient

class ViewRecipeIngredientAdapter :
    RecyclerView.Adapter<ViewRecipeIngredientAdapter.ViewHolder>(),
    BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()
    private var isEditAvailable = false
    private var listener: OnRecipeViewDeleteIngredient? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_add_new_recipe_ingredients_old, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setEditMode(allow: Boolean) {
        isEditAvailable = allow
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnRecipeViewDeleteIngredient) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipeIngredient: RecipeIngredient) {
            itemView.textView_ingredient_in_recipe.text = recipeIngredient.ingredient!!.title
            val quantityName =
                recipeIngredient.quantity.toString() + " " + recipeIngredient.unit!!.title
            itemView.textView_rv_add_recipe_ingredient_quantity.text = quantityName
            if (isEditAvailable) {
                itemView.btn_rv_add_recipe_ingredient_remove.visibility = View.VISIBLE
            } else {
                itemView.btn_rv_add_recipe_ingredient_remove.visibility = View.INVISIBLE
            }
            itemView.btn_rv_add_recipe_ingredient_remove.setOnClickListener {
                listener!!.onDeleteIngredientClick(data[adapterPosition], adapterPosition)
            }
        }
    }
}