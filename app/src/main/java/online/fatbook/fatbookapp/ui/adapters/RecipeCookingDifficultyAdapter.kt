package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rv_difficulty.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener

class RecipeCookingDifficultyAdapter :
    RecyclerView.Adapter<RecipeCookingDifficultyAdapter.ViewHolder>(),
    BindableAdapter<CookingDifficulty> {

    private var data: List<CookingDifficulty> = ArrayList()
    var listener: OnRecipeDifficultyClickListener? = null
    var selectedDifficulty: CookingDifficulty? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_difficulty, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<CookingDifficulty>?) {
        data?.let {
            this.data = it as ArrayList<CookingDifficulty>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnRecipeDifficultyClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingDifficulty?) {
            if (selectedDifficulty!!.title == value!!.title) {
                selectItem(
                    itemView.cardview_rv_difficulty,
                    itemView.textview_item_title_rv_difficulty
                )
            } else {
                unselectItem(
                    itemView.cardview_rv_difficulty,
                    itemView.textview_item_title_rv_difficulty
                )
            }

            itemView.textview_item_title_rv_difficulty.text = value.title

            if (itemView.cardview_rv_difficulty.isClickable) {
                itemView.cardview_rv_difficulty.setOnClickListener {
                    listener!!.onRecipeDifficultyClick(
                        data.indexOf(selectedDifficulty),
                        bindingAdapterPosition,
                        value
                    )
                }
            }
        }
    }

    private fun selectItem(cardView: MaterialCardView, textView: TextView) {
        cardView.setBackgroundResource(R.drawable.select_search_item_round_corner)
        cardView.isClickable = false
        textView.isSelected = true
    }

    private fun unselectItem(cardView: MaterialCardView, textView: TextView) {
        cardView.setBackgroundResource(R.drawable.unselect_search_item_round_corner)
        cardView.isClickable = true
        textView.isSelected = false
    }
}
