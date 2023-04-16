package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.RvRecipeIngredientBinding
import online.fatbook.fatbookapp.ui.recipe.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.util.FormatUtils
import org.apache.commons.lang3.StringUtils

class RecipeIngredientAdapter(private val context: Context) :
    RecyclerView.Adapter<RecipeIngredientAdapter.RecipeIngredientItemViewHolder>(),
    BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()
    var listener: OnRecipeIngredientItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeIngredientItemViewHolder {
        val binding = RvRecipeIngredientBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecipeIngredientItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeIngredientItemViewHolder, position: Int) {
        val ingredientItem = data[position]
        holder.bind(ingredientItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it as ArrayList<RecipeIngredient>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnRecipeIngredientItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecipeIngredientItemViewHolder(rvRecipeIngredientBinding: RvRecipeIngredientBinding) :
        RecyclerView.ViewHolder(rvRecipeIngredientBinding.root) {
        private val binding = rvRecipeIngredientBinding
        fun bind(ingredientItem: RecipeIngredient?) {
            binding.textviewIngredientTitleRvAddedIngredient.text =
                ingredientItem!!.ingredient!!.title

            ingredientItem.kcal.let {
                if (ingredientItem.kcal == 0.0 && ingredientItem.fats == 0.0 && ingredientItem.carbs == 0.0 && ingredientItem.proteins == 0.0) {
                    binding.textviewIngredientKcalsTitleRvAddedIngredient.text =
                        StringUtils.EMPTY
                } else {
                    binding.textviewIngredientKcalsTitleRvAddedIngredient.text =
                        String.format("%s kcal", FormatUtils.prettyCount(it))
                }
            }

            binding.textviewIngredientQtyTitleRvAddedIngredient.text =
                String.format(
                    "%s %s",
                    FormatUtils.prettyCount(ingredientItem.quantity!!),
                    ingredientItem.unit!!.title
                )

            binding.buttonRemoveRvAddedIngredient.setOnClickListener {
                listener!!.onRecipeIngredientDelete(bindingAdapterPosition)
            }
        }
    }

}
