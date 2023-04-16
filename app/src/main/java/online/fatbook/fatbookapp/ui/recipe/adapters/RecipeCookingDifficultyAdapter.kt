package online.fatbook.fatbookapp.ui.recipe.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingDifficulty
import online.fatbook.fatbookapp.databinding.RvDifficultyBinding
import online.fatbook.fatbookapp.ui.recipe.listeners.OnRecipeDifficultyClickListener
import online.fatbook.fatbookapp.util.BindableAdapter

class RecipeCookingDifficultyAdapter(private val context: Context) :
    RecyclerView.Adapter<RecipeCookingDifficultyAdapter.CookingDifficultyItemViewHolder>(),
    BindableAdapter<CookingDifficulty> {

    private var data: List<CookingDifficulty> = ArrayList()
    var listener: OnRecipeDifficultyClickListener? = null
    var selectedDifficulty: CookingDifficulty? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CookingDifficultyItemViewHolder {
        val binding = RvDifficultyBinding.inflate(LayoutInflater.from(context), parent, false)
        return CookingDifficultyItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CookingDifficultyItemViewHolder, position: Int) {
        val difficultyItem = data[position]
        holder.bind(difficultyItem)
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

    inner class CookingDifficultyItemViewHolder(rvDifficultyBinding: RvDifficultyBinding) :
        RecyclerView.ViewHolder(rvDifficultyBinding.root) {
        private val binding = rvDifficultyBinding
        fun bind(difficultyItem: CookingDifficulty?) {
            if (selectedDifficulty!!.title == difficultyItem!!.title) {
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

            binding.textviewItemTitleRvDifficulty.text = difficultyItem.title

            if (binding.cardviewRvDifficulty.isClickable) {
                binding.cardviewRvDifficulty.setOnClickListener {
                    listener!!.onRecipeDifficultyClick(
                        data.indexOf(selectedDifficulty),
                        bindingAdapterPosition,
                        difficultyItem
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
