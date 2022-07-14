package online.fatbook.fatbookapp.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import lombok.extern.java.Log
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.Ingredient
import online.fatbook.fatbookapp.databinding.FragmentIngredientsBinding
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Log
class IngredientsFragment : Fragment() {
    private var binding: FragmentIngredientsBinding? = null
    private var ingredientToAdd: Ingredient? = null
    private var ingredientList: List<Ingredient>? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var adapter: IngredientsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding!!.fabIngredientsAdd.setOnClickListener {
            configureAlertDialog()
        }
        ingredientViewModel = ViewModelProvider(requireActivity())[IngredientViewModel::class.java]
        ingredientList = ArrayList()
        setupAdapter()
        if (ingredientViewModel!!.ingredientList.value == null) {
            loadIngredients()
        }
        setupSwipeRefresh()
        ingredientViewModel!!.ingredientList.observe(viewLifecycleOwner) { ingredients: List<Ingredient?>? ->
            binding!!.swipeRefreshBookmarks.isRefreshing = false
            ingredientList = ingredientViewModel!!.ingredientList.value
            adapter!!.setData(ingredientList)
            adapter!!.notifyDataSetChanged()
        }
        //
//        binding.editTextSearchIngredients.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                filter(editable.toString());
//            }
//        });
//    }
//
//    private void filter(String text) {
//        List<Ingredient> temp = new ArrayList<>();
//        for (Ingredient i : ingredientList) {
//            if (StringUtils.containsIgnoreCase(i.getName(), text)) {
//                temp.add(i);
//            }
//        }
//        adapter.updateList(temp);
    }

    private fun setupAdapter() {
        val rv = binding!!.rvIngredients
        adapter = IngredientsAdapter()
        adapter!!.setData(ingredientList)
        rv.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding!!.swipeRefreshBookmarks.setColorSchemeColors(
            resources.getColor(R.color.color_pink_a200)
        )
        binding!!.swipeRefreshBookmarks.setOnRefreshListener { loadIngredients() }
    }

    private fun saveIngredient() {
        try {
            RetrofitFactory.apiServiceClient().addIngredient(ingredientToAdd)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.code() == 201) {
                            Toast.makeText(requireContext(), "Ingredient added", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Ingredient with that name already exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
//                        IngredientsFragment.log.log(
//                            Level.INFO,
//                            "ingredient save: " + response.code()
//                        )
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
//                        IngredientsFragment.log.log(Level.INFO, "ingredient save: FAILED")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadIngredients() {
        RetrofitFactory.apiServiceClient().allIngredients.enqueue(object :
            Callback<List<Ingredient>?> {
            override fun onResponse(
                call: Call<List<Ingredient>?>,
                response: Response<List<Ingredient>?>
            ) {
                ingredientViewModel!!.ingredientList.value = response.body()
//                IngredientsFragment.log.log(Level.INFO, "ingredient list load: SUCCESS")
            }

            override fun onFailure(call: Call<List<Ingredient>?>, t: Throwable) {
//                IngredientsFragment.log.log(Level.INFO, "ingredient list load: FAILED")
                showErrorMsg()
            }
        })
    }

    private fun showErrorMsg() {
        Toast.makeText(
            binding!!.root.context,
            resources.getString(R.string.ingredient_load_failed),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun configureAlertDialog() {
        val editTextName = EditText(requireContext())
        editTextName.setSingleLine()
        editTextName.setHintTextColor(resources.getColor(R.color.color_blue_grey_200))
        editTextName.setTextColor(resources.getColor(R.color.color_blue_grey_600))
        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editTextName.layoutParams = params
        container.addView(editTextName)
        val title = LayoutInflater.from(requireContext())
            .inflate(R.layout.alert_dialog_title_ingredient, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(container)
            .setCustomTitle(title)
            .setPositiveButton(resources.getString(R.string.alert_dialog_btn_ok), null)
            .setNegativeButton(resources.getString(R.string.alert_dialog_btn_cancel)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            .create()
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val button = (dialog as AlertDialog).getButton(
                AlertDialog.BUTTON_POSITIVE
            )
            button.setOnClickListener { view: View? ->
                val name = editTextName.text.toString()
                if (StringUtils.isNotEmpty(name) && name.length >= 3) {
                    ingredientToAdd = Ingredient()
                    ingredientToAdd!!.name = name
                    saveIngredient()
                    dialog.dismiss()
                } else {
                    editTextName.setText(StringUtils.EMPTY)
                    editTextName.hint =
                        resources.getString(R.string.alert_dialog_suggest_ingredient)
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        binding!!.root.viewTreeObserver.addOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    }

    override fun onPause() {
        super.onPause()
        binding!!.root.viewTreeObserver.removeOnGlobalLayoutListener(
            KeyboardActionUtil(
                binding!!.root, requireActivity()
            ).listenerForAdjustResize
        )
    }
}