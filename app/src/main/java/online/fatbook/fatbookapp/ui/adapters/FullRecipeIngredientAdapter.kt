package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.RvIngredientsToRecipeViewBinding
import online.fatbook.fatbookapp.util.FormatUtils.roundOffDecimal

class FullRecipeIngredientAdapter : RecyclerView.Adapter<FullRecipeIngredientAdapter.ViewHolder>(),
    BindableAdapter<RecipeIngredient> {
    private var _binding: RvIngredientsToRecipeViewBinding? = null
    private val binding get() = _binding!!

    private var data: List<RecipeIngredient> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvIngredientsToRecipeViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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