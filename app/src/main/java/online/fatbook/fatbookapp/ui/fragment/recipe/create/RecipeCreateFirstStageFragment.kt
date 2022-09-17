package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import kotlin.math.min


class RecipeCreateFirstStageFragment : Fragment() {
    private var binding: FragmentRecipeCreateFirstStageBinding? = null

    private var cooking_time_hours = 0
    private var cooking_time_min = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateFirstStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.WRAP_CONTENT)
        button_delete_photo_recipe_create_1_stage.setOnClickListener {
            configureAlertDialog()
        }

    }

    private fun configureAlertDialog() {
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_start)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_end)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_timepicker)
            .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok), null)
            //.setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val picker = dialog.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
            picker.setIs24HourView(true)
            picker.setOnTimeChangedListener { _, hourOfDay, minute ->
                cooking_time_hours = hourOfDay
                cooking_time_min = minute
            }

        setTime(cooking_time_hours, cooking_time_min)
        }

        private fun setTime(hours: Int, minute: Int) {
            if (hours == 0 && minute == 0) {
                edittext_time_recipe_create_1_stage.setText(getString(R.string.default_cooking_time))
            }
            if (hours != 0 || minute != 0) {
                val _hours = String.format(getString(R.string.set_cooking_time_tmpl_hours), hours)
                val _minute = String.format(getString(R.string.set_cooking_time_tmpl_min), minute)
                val str = String.format("%d h, %d min", _hours, _minute)
                edittext_time_recipe_create_1_stage.setText(str)
            }

        }

    }