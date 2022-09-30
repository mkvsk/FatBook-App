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
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_recipe_create_first_stage.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.databinding.FragmentRecipeCreateFirstStageBinding
import online.fatbook.fatbookapp.ui.viewmodel.StaticDataViewModel
import online.fatbook.fatbookapp.util.obtainViewModel

class RecipeCreateFirstStageFragment : Fragment() {
    private var binding: FragmentRecipeCreateFirstStageBinding? = null

    private val staticDataViewModel by lazy { obtainViewModel(StaticDataViewModel::class.java) }

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
        textview_set_time_recipe_create_1_stage.setOnClickListener {
            configureAlertDialog()
        }


        textview_cooking_method_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = true
            navigation(false)
        }

        textview_category_recipe_create_1_stage.setOnClickListener {
            staticDataViewModel.loadCookingMethod.value = false
            navigation(false)
        }

        button_next_recipe_create_1_stage.setOnClickListener {
            navigation(true)
        }
    }

    private fun navigation(nextStep: Boolean) {
        if (nextStep) {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_recipe_create_second_stage_from_first_stage)
        } else {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_go_to_recipe_choose_method_or_category_from_first_stage)
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
            .setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        val picker = dialog.findViewById<TimePicker>(R.id.timepicker_dialog_cooking_time)
        picker.setIs24HourView(true)

        picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            if (hourOfDay == 0 && minute == 0) {
                textview_set_time_recipe_create_1_stage.text =
                    getString(R.string.default_cooking_time)
            }
            if (hourOfDay != 0 && minute != 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d h %d min", hourOfDay, minute)
            }
            if (hourOfDay != 0 && minute == 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d h", hourOfDay, minute)
            }
            if (hourOfDay == 0 && minute != 0) {
                textview_set_time_recipe_create_1_stage.text =
                    String.format("%d min", minute)
            }
        }
    }
}
