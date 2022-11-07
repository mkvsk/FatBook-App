package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_recipe_ingredient.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.ui.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.util.FormatUtils

class RecipeIngredientAdapter :
    RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>(), BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()
    var listener: OnRecipeIngredientItemClickListener? = null

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_recipe_ingredient, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RecipeIngredient>?) {
        data?.let {
            this.data = it as ArrayList<RecipeIngredient>
            notifyDataSetChanged()
        }
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun setClickListener(listener: OnRecipeIngredientItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: RecipeIngredient?) {
            itemView.textview_ingredient_title_rv_added_ingredient.text = value!!.ingredient!!.title

            value.kcal?.let {
                itemView.textview_ingredient_kcals_title_rv_added_ingredient.text =
                    String.format("%s kcal", FormatUtils.prettyCountNutritionFacts(it))
            }

            itemView.textview_ingredient_qtt_title_rv_added_ingredient.text =
                String.format("%s %s", FormatUtils.prettyCount(value.quantity!!), value.unit!!.getDisplayName(context))

            itemView.button_remove_rv_added_ingredient.setOnClickListener {
                listener!!.onRecipeIngredientDelete(bindingAdapterPosition)
            }
        }
    }

}
