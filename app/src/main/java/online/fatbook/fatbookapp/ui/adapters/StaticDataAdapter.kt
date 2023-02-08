package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.RvRecipeMethodsCategoriesItemsBinding
import online.fatbook.fatbookapp.ui.listeners.OnStaticDataClickListener

class StaticDataAdapter :
    RecyclerView.Adapter<StaticDataAdapter.ViewHolder>(), BindableAdapter<StaticDataObject> {
    private var _binding: RvRecipeMethodsCategoriesItemsBinding? = null
    private val binding get() = _binding!!

    private var data: List<StaticDataObject> = ArrayList()
    var listener: OnStaticDataClickListener? = null
    var selectedItems: List<Int>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvRecipeMethodsCategoriesItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<StaticDataObject>?) {
        data?.let {
            this.data = it as ArrayList<StaticDataObject>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnStaticDataClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelected(arrayList: List<Int>) {
        selectedItems = arrayList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StaticDataObject?) {
            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
            } else {
                unselectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
            }

            binding.textviewRvRecipeMethodsCategoriesItems.text = value!!.title

            binding.cardviewRvRecipeMethodsCategoriesItems.setOnClickListener {
                if (value is CookingMethod) {
                    listener?.onItemClick(data[bindingAdapterPosition])
                } else {
                    if (!binding.textviewRvRecipeMethodsCategoriesItems.isSelected) {
                        selectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
                        binding.textviewRvRecipeMethodsCategoriesItems.isSelected = true
                    } else {
                        unselectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
                        binding.textviewRvRecipeMethodsCategoriesItems.isSelected = false
                    }
                    listener?.onItemClickChoose(data[bindingAdapterPosition])
                }
            }
        }
    }

    private fun selectItem(cardView: MaterialCardView) {
        cardView.isChecked = true
        cardView.isSelected = true
        cardView.strokeWidth = 5
    }

    private fun unselectItem(cardView: MaterialCardView) {
        cardView.isChecked = false
        cardView.isSelected = false
        cardView.strokeWidth = 0
    }
}
