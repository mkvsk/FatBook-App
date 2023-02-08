package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.databinding.RvDifficultyBinding
import online.fatbook.fatbookapp.ui.listeners.OnRecipeDifficultyClickListener

class RecipeCookingDifficultyAdapter :
    RecyclerView.Adapter<RecipeCookingDifficultyAdapter.ViewHolder>(),
    BindableAdapter<CookingDifficulty> {
    private var _binding: RvDifficultyBinding? = null
    private val binding get() = _binding!!

    private var data: List<CookingDifficulty> = ArrayList()
    var listener: OnRecipeDifficultyClickListener? = null
    var selectedDifficulty: CookingDifficulty? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            RvDifficultyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
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
                    binding.cardviewRvDifficulty,
                    binding.textviewItemTitleRvDifficulty
                )
            } else {
                unselectItem(
                    binding.cardviewRvDifficulty,
                    binding.textviewItemTitleRvDifficulty
                )
            }

            binding.textviewItemTitleRvDifficulty.text = value.title

            if (binding.cardviewRvDifficulty.isClickable) {
                binding.cardviewRvDifficulty.setOnClickListener {
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
