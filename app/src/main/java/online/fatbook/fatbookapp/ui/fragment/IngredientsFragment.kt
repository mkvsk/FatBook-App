package online.fatbook.fatbookapp.ui.fragment

import androidx.lifecycle.ViewModelProvider.get
import androidx.navigation.NavController.navigate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.NavController.popBackStack
import androidx.appcompat.app.AppCompatActivity
import online.fatbook.fatbookapp.ui.viewmodel.RecipeViewModel
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.ui.viewmodel.IngredientViewModel
import androidx.navigation.NavController
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import online.fatbook.fatbookapp.util.UserUtils
import online.fatbook.fatbookapp.R
import android.content.SharedPreferences
import com.google.android.material.navigation.NavigationBarView
import online.fatbook.fatbookapp.ui.viewmodel.SignInViewModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import android.content.Intent
import online.fatbook.fatbookapp.ui.activity.PasswordActivity
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.content.res.AppCompatResources
import online.fatbook.fatbookapp.ui.activity.LoginActivity
import online.fatbook.fatbookapp.retrofit.RetrofitFactory
import online.fatbook.fatbookapp.ui.activity.MainActivity
import online.fatbook.fatbookapp.ui.activity.SignInActivity
import online.fatbook.fatbookapp.ui.activity.WelcomeActivity
import online.fatbook.fatbookapp.ui.activity.SplashActivity
import androidx.activity.result.ActivityResultLauncher
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoViewModel
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.ActivityResultCallback
import online.fatbook.fatbookapp.ui.activity.fill_additional_info.FillAdditionalInfoActivity
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import online.fatbook.fatbookapp.ui.activity.SkipAdditionalInfoActivity
import android.widget.Toast
import online.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.util.RecipeUtils
import android.widget.TextView
import android.widget.ImageButton
import online.fatbook.fatbookapp.core.Ingredient
import online.fatbook.fatbookapp.core.RecipeIngredient
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient
import online.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener
import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.cardview.widget.CardView
import online.fatbook.fatbookapp.ui.listeners.OnRecipeRevertDeleteListener
import online.fatbook.fatbookapp.ui.adapters.RecipeAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.SimpleItemAnimator
import online.fatbook.fatbookapp.ui.fragment.FeedFragment
import androidx.navigation.fragment.NavHostFragment
import online.fatbook.fatbookapp.util.KeyboardActionUtil
import online.fatbook.fatbookapp.ui.fragment.BookmarksFragment
import online.fatbook.fatbookapp.ui.adapters.ViewRecipeIngredientAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeViewFragment
import okhttp3.RequestBody
import okhttp3.MultipartBody
import android.widget.FrameLayout
import android.content.DialogInterface
import online.fatbook.fatbookapp.ui.adapters.IngredientsAdapter
import online.fatbook.fatbookapp.ui.fragment.IngredientsFragment
import android.widget.EditText
import android.content.DialogInterface.OnShowListener
import com.google.android.material.appbar.AppBarLayout
import android.graphics.PorterDuff
import androidx.fragment.app.FragmentActivity
import online.fatbook.fatbookapp.ui.fragment.UserProfileFragment
import online.fatbook.fatbookapp.ui.fragment.RecipeCreateFragment
import online.fatbook.fatbookapp.ui.adapters.AddIngredientToRecipeAdapter
import online.fatbook.fatbookapp.ui.fragment.RecipeAddIngredientFragment
import online.fatbook.fatbookapp.core.IngredientUnit
import androidx.lifecycle.ViewModel
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Environment
import android.text.TextUtils
import android.content.ContentUris
import android.provider.OpenableColumns
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import lombok.extern.java.Log
import online.fatbook.fatbookapp.databinding.FragmentIngredientsBinding
import online.fatbook.fatbookapp.retrofit.NetworkInfoService
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import java.lang.Exception
import java.util.ArrayList
import java.util.logging.Level

@Log
class IngredientsFragment : Fragment() {
    private var binding: FragmentIngredientsBinding? = null
    private var ingredientToAdd: Ingredient? = null
    private var ingredientList: List<Ingredient?>? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var adapter: IngredientsAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding!!.fabIngredientsAdd.setOnClickListener { view1: View? -> configureAlertDialog() }
        ingredientViewModel = ViewModelProvider(requireActivity()).get(
            IngredientViewModel::class.java
        )
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
        adapter = IngredientsAdapter(binding!!.root.context, ingredientList)
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
                        IngredientsFragment.log.log(
                            Level.INFO,
                            "ingredient save: " + response.code()
                        )
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        IngredientsFragment.log.log(Level.INFO, "ingredient save: FAILED")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadIngredients() {
        RetrofitFactory.apiServiceClient().allIngredients.enqueue(object :
            Callback<List<Ingredient?>?> {
            override fun onResponse(
                call: Call<List<Ingredient?>?>,
                response: Response<List<Ingredient?>?>
            ) {
                ingredientViewModel!!.setIngredientList(response.body())
                IngredientsFragment.log.log(Level.INFO, "ingredient list load: SUCCESS")
            }

            override fun onFailure(call: Call<List<Ingredient?>?>, t: Throwable) {
                IngredientsFragment.log.log(Level.INFO, "ingredient list load: FAILED")
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
                    ingredientToAdd.setName(name)
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
    ): View? {
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