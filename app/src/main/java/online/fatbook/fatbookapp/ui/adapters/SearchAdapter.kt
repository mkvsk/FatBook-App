package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_search.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.StaticDataObject
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import online.fatbook.fatbookapp.util.Constants.TAG_SELECT_ALL_BUTTON
import kotlin.math.log

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>(),
    BindableAdapter<StaticDataObject> {

    private var data: List<StaticDataObject> = ArrayList()
    var listener: OnSearchItemClickListener? = null
    var selectedItems: ArrayList<Int>? = ArrayList()
    var isAllSelected: Boolean = false

    companion object{
        private const val TAG = "SearchAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_search, parent, false)
        )
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StaticDataObject?) {
            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
            } else {
                unselectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
            }
            itemView.textview_item_title_rv_search.text = value!!.title
            itemView.cardview_rv_search.setOnClickListener {

                if (value.tag == TAG_SELECT_ALL_BUTTON) {
                    if (!itemView.textview_item_title_rv_search.isSelected) {
                        isAllSelected = true
                        selectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
                        val list: ArrayList<Int> = ArrayList()
                        for (i in data.indices) {
                            list.add(i)
                        }
                        selectedItems!!.clear()
                        setSelected(list)
                    } else {
                        isAllSelected = false
                        unselectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
                        setSelected(ArrayList())
                    }
                    listener!!.onSelectAllClick()
                } else {
                    if (!itemView.textview_item_title_rv_search.isSelected) {
                        addToSelected(bindingAdapterPosition)
                        if (selectedItems!!.size == data.size - 1) {
                            addToSelected(0)
                            isAllSelected = true
                        }
                    } else {
                        removeFromSelected(bindingAdapterPosition)
                        if (isAllSelected) {
                            removeFromSelected(0)
                            isAllSelected = false
                        }
                    }
                    listener?.onItemClick(value)
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
