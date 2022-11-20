package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_search.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.StaticDataBase
import online.fatbook.fatbookapp.ui.listeners.OnSearchItemClickListener
import org.apache.commons.lang3.StringUtils

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>(),
    BindableAdapter<StaticDataBase> {

    private var data: List<StaticDataBase> = ArrayList()
    var listener: OnSearchItemClickListener? = null
    var selectedItems: List<Int>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_search, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<StaticDataBase>?) {
        data?.let {
            this.data = it as ArrayList<StaticDataBase>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnSearchItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelected(arrayList: List<Int>) {
        selectedItems = arrayList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StaticDataBase?) {

            if (selectedItems!!.contains(bindingAdapterPosition)) {
                selectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
            } else {
                unselectItem(itemView.cardview_rv_search, itemView.textview_item_title_rv_search)
            }

            itemView.textview_item_title_rv_search.text = value!!.title

            itemView.cardview_rv_search.setOnClickListener {

                if (StringUtils.equalsIgnoreCase(value.title, "select all")) {
                    val list: ArrayList<Int> = ArrayList()
                    if (selectedItems!!.size != data.size + 1) {
                        for (i in 0..data.size) {
                            list.add(i)
                            setSelected(list)
                        }
                    } else {
                        setSelected(list)
                    }
                } else {
                    if (!itemView.textview_item_title_rv_search.isSelected) {
                        selectItem(
                            itemView.cardview_rv_search,
                            itemView.textview_item_title_rv_search
                        )
                    } else {
                        unselectItem(
                            itemView.cardview_rv_search, itemView.textview_item_title_rv_search
                        )
                    }
                    listener?.onItemClick(data[bindingAdapterPosition])
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
}
