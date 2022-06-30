package online.fatbook.fatbookapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.RecipeIngredient
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient

class ViewRecipeIngredientAdapter(
    context: Context?,
    list: ArrayList<RecipeIngredient>,
    listener: OnRecipeViewDeleteIngredient
) : RecyclerView.Adapter<ViewRecipeIngredientAdapter.ViewHolder>(),
    BindableAdapter<RecipeIngredient> {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var list: List<RecipeIngredient>
    private var isEditAvailable = false
    private val listener: OnRecipeViewDeleteIngredient
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_add_new_recipe_ingredients, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = list[position]
        holder.name.text = ingredient.ingredient!!.name
        val quantityName =
            ingredient.quantity.toString() + " " + ingredient.unit!!.getDisplayName(inflater.context)
        holder.quantity.text = quantityName
        if (isEditAvailable) {
            holder.btnRemove.visibility = View.VISIBLE
        } else {
            holder.btnRemove.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setEditMode(allow: Boolean) {
        isEditAvailable = allow
    }

    override fun setData(data: List<RecipeIngredient>?) {
        data.let {
            list = it as ArrayList<RecipeIngredient>
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textView_ingredient_in_recipe)
        val quantity: TextView =
            itemView.findViewById(R.id.textView_rv_add_recipe_ingredient_quantity)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_rv_add_recipe_ingredient_remove)

        init {
            btnRemove.setOnClickListener {
                listener.onDeleteIngredientClick(
                    list[adapterPosition], adapterPosition
                )
            }
        }
    }

    init {
        this.list = list
        this.listener = listener
    }
}