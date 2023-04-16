package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.RvIngredientsToRecipeViewBinding
import online.fatbook.fatbookapp.util.FormatUtils.roundOffDecimal

class FullRecipeIngredientAdapter(private val context: Context) :
    RecyclerView.Adapter<FullRecipeIngredientAdapter.FullRecipeIngredientItemViewHolder>(),
    BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FullRecipeIngredientItemViewHolder {
        val binding =
            RvIngredientsToRecipeViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return FullRecipeIngredientItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FullRecipeIngredientItemViewHolder, position: Int) {
        val ingredientItem = data[position]
        holder.bind(ingredientItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    inner class FullRecipeIngredientItemViewHolder(rvIngredientsToRecipeViewBinding: RvIngredientsToRecipeViewBinding) :
        RecyclerView.ViewHolder(rvIngredientsToRecipeViewBinding.root) {
        private val binding = rvIngredientsToRecipeViewBinding
        fun bind(recipeIngredient: RecipeIngredient) {
            binding.textviewIngredientTitleRvIngredientRecipeView.text =
                recipeIngredient.ingredient?.title

            binding.textviewIngredientKcalsTitleRvIngredientRecipeView.text =
                String.format("(%s kcal)", roundOffDecimal(recipeIngredient.kcal))

            binding.textviewIngredientQtyTitleRvIngredientRecipeView.text = String.format(
                "%s %s",
                roundOffDecimal(recipeIngredient.quantity!!).toString(),
                recipeIngredient.ingredient?.unitRatio?.unit?.title.toString()
            )
        }
    }

}