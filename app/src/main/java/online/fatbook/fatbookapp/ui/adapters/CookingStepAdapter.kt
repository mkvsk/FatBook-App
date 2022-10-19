package online.fatbook.fatbookapp.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_cooking_step_preview.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.recipe.CookingStep
import online.fatbook.fatbookapp.ui.listeners.OnCookingStepClickListener
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel

class CookingStepAdapter :
    RecyclerView.Adapter<CookingStepAdapter.ViewHolder>(), BindableAdapter<CookingStep> {

    private var data: ArrayList<CookingStep> = ArrayList()
    var listener: OnCookingStepClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_cooking_step_preview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<CookingStep>?) {
        data?.let {
            this.data = it as ArrayList<CookingStep>
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnCookingStepClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: CookingStep?) {

            itemView.textview_description_rv_cooking_step.text = value!!.description

//            itemView.cardview_cooking_step.isClickable
//            itemView.cardview_cooking_step.setOnClickListener {
//                listener!!.onCookingStepClick(
//                    data.indexOf(selectedStep),
//                    bindingAdapterPosition,
//                    value
//                )
//            }

            itemView.button_remove_rv_cooking_step.setOnClickListener {
                listener!!.onRecipeCookingStepDelete(bindingAdapterPosition)

                Log.d("REMOVE STEP:", "remover step number= ${value.stepNumber}")
            }

            itemView.cardview_cooking_step.setOnClickListener {
                listener!!.onCookingStepClick(value, bindingAdapterPosition)
            }
        }
    }
}
