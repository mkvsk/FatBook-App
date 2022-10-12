package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_recipe_ingredient.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.ui.listeners.OnRecipeIngredientItemClickListener

class RecipeIngredientAdapter :
    RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>(), BindableAdapter<RecipeIngredient> {

    private var data: List<RecipeIngredient> = ArrayList()
    var listener: OnRecipeIngredientItemClickListener? = null

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

    fun setClickListener(listener: OnRecipeIngredientItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    //TODO заполнить данные
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: RecipeIngredient?) {
            itemView.textview_ingredient_title_rv_added_ingredient.text = value!!.ingredient!!.title

            val kcal = value.ingredient!!.units?.get(0)?.kcal.toString()
            itemView.textview_ingredient_kcals_title_rv_added_ingredient.text =
                String.format("%s kcal", kcal)

            val qtt = value.ingredient!!.units?.get(0)?.amount.toString()
            itemView.textview_ingredient_qtt_title_rv_added_ingredient.text =
                String.format("%s gr", qtt)


            itemView.button_remove_rv_added_ingredient.setOnClickListener {
                listener!!.onRecipeIngredientDelete(bindingAdapterPosition)
            }
        }
    }

}
