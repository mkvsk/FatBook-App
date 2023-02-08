package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import online.fatbook.fatbookapp.core.recipe.ingredient.RecipeIngredient
import online.fatbook.fatbookapp.databinding.RvRecipeIngredientBinding
import online.fatbook.fatbookapp.ui.listeners.OnRecipeIngredientItemClickListener
import online.fatbook.fatbookapp.util.FormatUtils
import org.apache.commons.lang3.StringUtils

class RecipeIngredientAdapter :
    RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>(), BindableAdapter<RecipeIngredient> {
    private var _binding: RvRecipeIngredientBinding? = null
    private val binding get() = _binding!!

    private var data: List<RecipeIngredient> = ArrayList()
    var listener: OnRecipeIngredientItemClickListener? = null

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvRecipeIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
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
            binding.textviewIngredientTitleRvAddedIngredient.text = value!!.ingredient!!.title

            value.kcal.let {
                if (value.kcal == 0.0 && value.fats == 0.0 && value.carbs == 0.0 && value.proteins == 0.0) {
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
                    FormatUtils.prettyCount(value.quantity!!),
                    value.unit!!.title
                )

            binding.buttonRemoveRvAddedIngredient.setOnClickListener {
                listener!!.onRecipeIngredientDelete(bindingAdapterPosition)
            }
        }
    }

}
