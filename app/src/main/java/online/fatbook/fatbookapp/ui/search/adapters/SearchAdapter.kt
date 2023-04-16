package online.fatbook.fatbookapp.ui.search.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.databinding.RvSearchBinding
import online.fatbook.fatbookapp.ui.search.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.util.BindableAdapter
import online.fatbook.fatbookapp.util.Constants.TAG_SELECT_ALL_BUTTON

class SearchAdapter(private val context: Context) :
    RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder>(),
    BindableAdapter<StaticDataObject> {

    private var data: List<StaticDataObject> = ArrayList()
    var listener: OnSearchItemClickListener? = null
    var selectedItems: ArrayList<Int>? = ArrayList()
    var isAllSelected: Boolean = false

    companion object {
        private const val TAG = "SearchAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val binding = RvSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val searchItem = data[position]
        holder.bind(searchItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<StaticDataObject>?) {
        data?.let {
            this.data = it as ArrayList<StaticDataObject>
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectAll(selectAll: StaticDataObject) {
        (data as ArrayList).add(0, selectAll)
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnSearchItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelected(arrayList: ArrayList<Int>) {
        selectedItems = arrayList
        notifyDataSetChanged()
    }

    private fun addToSelected(position: Int) {
        selectedItems!!.add(position)
        notifyItemChanged(position)
    }

    private fun removeFromSelected(position: Int) {
        selectedItems!!.remove(position)
        notifyItemChanged(position)
    }

    inner class SearchItemViewHolder(rvSearchBinding: RvSearchBinding) :
        RecyclerView.ViewHolder(rvSearchBinding.root) {
        private val binding = rvSearchBinding
        fun bind(searchItem: StaticDataObject?) {
            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(binding.cardviewRvSearch, binding.textviewItemTitleRvSearch)
            } else {
                unselectItem(binding.cardviewRvSearch, binding.textviewItemTitleRvSearch)
            }
            binding.textviewItemTitleRvSearch.text = searchItem!!.title
            binding.cardviewRvSearch.setOnClickListener {

                if (searchItem.tag == TAG_SELECT_ALL_BUTTON) {
                    if (!binding.textviewItemTitleRvSearch.isSelected) {
                        isAllSelected = true
                        selectItem(
                            binding.cardviewRvSearch,
                            binding.textviewItemTitleRvSearch
                        )
                        val list: ArrayList<Int> = ArrayList()
                        for (i in data.indices) {
                            list.add(i)
                        }
                        selectedItems!!.clear()
                        setSelected(list)
                    } else {
                        isAllSelected = false
                        unselectItem(
                            binding.cardviewRvSearch,
                            binding.textviewItemTitleRvSearch
                        )
                        setSelected(ArrayList())
                    }
                    listener!!.onSelectAllClick()
                } else {
                    if (!binding.textviewItemTitleRvSearch.isSelected) {
                        addToSelected(bindingAdapterPosition)
                        if (searchItem !is CookingDifficulty) {
                            if (selectedItems!!.size == data.size - 1) {
                                addToSelected(0)
                                isAllSelected = true
                            }
                        }
                    } else {
                        removeFromSelected(bindingAdapterPosition)
                        if (isAllSelected) {
                            removeFromSelected(0)
                            isAllSelected = false
                        }
                    }
                    listener?.onItemClick(searchItem)
                }
            }
        }
    }

    private fun selectItem(cardView: MaterialCardView, textView: TextView) {
        cardView.setBackgroundResource(R.drawable.select_search_item_round_corner)
        textView.isSelected = true
    }

    private fun unselectItem(cardView: MaterialCardView, textView: TextView) {
        cardView.setBackgroundResource(R.drawable.unselect_search_item_round_corner)
        textView.isSelected = false
    }

    private fun toggleSelectAll(cardView: MaterialCardView, textView: TextView) {
        if (isAllSelected) {
            selectItem(cardView, textView)
        } else {
            unselectItem(cardView, textView)
        }
    }
}
