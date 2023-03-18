package online.fatbook.fatbookapp.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_ingredients_to_recipe_view.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.util.FormatUtils.roundOffDecimal

class FullRecipeIngredientAdapter : RecyclerView.Adapter<FullRecipeIngredientAdapter.ViewHolder>(),
    BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_ingredients_to_recipe_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipeIngredient: RecipeIngredient) {
            itemView.textview_ingredient_title_rv_ingredient_recipe_view.text =
                recipeIngredient.ingredient?.title

            itemView.textview_ingredient_kcals_title_rv_ingredient_recipe_view.text =
                String.format("(%s kcal)", roundOffDecimal(recipeIngredient.kcal))

            itemView.textview_ingredient_qty_title_rv_ingredient_recipe_view.text = String.format(
                "%s %s",
                roundOffDecimal(recipeIngredient.quantity!!).toString(),
                recipeIngredient.ingredient?.unitRatio?.unit?.title.toString()
            )
        }
    }

}