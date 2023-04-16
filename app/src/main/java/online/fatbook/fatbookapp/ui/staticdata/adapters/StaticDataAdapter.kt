package online.fatbook.fatbookapp.ui.staticdata.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.core.recipe.CookingMethod
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.RvRecipeMethodsCategoriesItemsBinding
import online.fatbook.fatbookapp.ui.staticdata.listeners.OnStaticDataClickListener
import online.fatbook.fatbookapp.util.BindableAdapter

class StaticDataAdapter(private val context: Context) :
    RecyclerView.Adapter<StaticDataAdapter.StaticDataViewHolder>(),
    BindableAdapter<StaticDataObject> {

    private var staticDataList: List<StaticDataObject> = ArrayList()
    var listener: OnStaticDataClickListener? = null
    var selectedItems: List<Int>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaticDataViewHolder {
        val binding = RvRecipeMethodsCategoriesItemsBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return StaticDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StaticDataViewHolder, position: Int) {
        val staticDataItem = staticDataList[position]
        holder.bind(staticDataItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<StaticDataObject>?) {
        data?.let {
            this.staticDataList = it as ArrayList<StaticDataObject>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnStaticDataClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return staticDataList.size
    }

    fun setSelected(arrayList: List<Int>) {
        selectedItems = arrayList
    }

    inner class StaticDataViewHolder(rvRecipeMethodsCategoriesItemsBinding: RvRecipeMethodsCategoriesItemsBinding) :
        RecyclerView.ViewHolder(rvRecipeMethodsCategoriesItemsBinding.root) {
        private val binding = rvRecipeMethodsCategoriesItemsBinding
        fun bind(staticDataItem: StaticDataObject?) {
            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
            } else {
                unselectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
            }

            binding.textviewRvRecipeMethodsCategoriesItems.text = staticDataItem!!.title

            binding.cardviewRvRecipeMethodsCategoriesItems.setOnClickListener {
                if (staticDataItem is CookingMethod) {
                    listener?.onItemClick(staticDataList[bindingAdapterPosition])
                } else {
                    if (!binding.textviewRvRecipeMethodsCategoriesItems.isSelected) {
                        selectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
                        binding.textviewRvRecipeMethodsCategoriesItems.isSelected = true
                    } else {
                        unselectItem(binding.cardviewRvRecipeMethodsCategoriesItems)
                        binding.textviewRvRecipeMethodsCategoriesItems.isSelected = false
                    }
                    listener?.onItemClickChoose(staticDataList[bindingAdapterPosition])
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
