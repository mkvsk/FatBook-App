package online.fatbook.fatbookapp.ui.fragment.recipe.create

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TimePicker
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.dialog_timepicker.*
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Ingredient
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import online.fatbook.fatbookapp.util.TimePickerFragment
import online.fatbook.fatbookapp.util.hideKeyboard
import org.apache.commons.lang3.StringUtils


class RecipeCreateFirstStageFragment : Fragment() {
    private var binding: FragmentRecipeCreateFirstStageBinding? = null

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

//            SnapTimePickerDialog.Builder().apply {
//                setTitle(R.string.title_add_recipe)
//                setThemeColor(R.color.colorAccent)
//                setTitleColor(R.color.colorPrimary)
//            }.build().show(parentFragmentManager, tag)

//            TimePickerFragment().show(parentFragmentManager, "time picker")

//            openTimePicker()


        }

    }

    private fun configureAlertDialog() {
//        val timePicker = timepicker_dialog_cooking_time
//        timePicker.setIs24HourView(true)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_start)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.cards_margin_end)

//        timepicker_dialog_cooking_time.setIs24HourView(true)


        val dialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_timepicker)
            .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok), null)
            .setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .create()

//        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
//            val button = (dialog as AlertDialog).getButton(
//                AlertDialog.BUTTON_POSITIVE
//            )
//        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val picker = dialog.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
        picker.setIs24HourView(true)
    }
//
//    private fun openTimePicker() {
//        val clockFormat = TimeFormat.CLOCK_24H
//
//        val picker = MaterialTimePicker.Builder()
////            .setTheme(R.style.TimePicker)
//            .setTimeFormat(clockFormat)
//            .setHour(0)
//            .setMinute(15)
//            .setTitleText("Set cooking time")
//            .build()
//        picker.show(childFragmentManager, "TAG")
//    }


    private fun setTime() {

    }

}