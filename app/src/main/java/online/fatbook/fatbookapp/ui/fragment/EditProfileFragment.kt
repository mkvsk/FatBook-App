package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_test.*
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.util.LoadingDialog
import online.fatbook.fatbookapp.util.obtainFragmentViewModel

class EditProfileFragment : Fragment() {
    private var binding: FragmentEditProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val loadingDialog = LoadingDialog(requireContext())

        loadingDialog.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadingDialog.isDismiss()
            }

        }, 5000)
*/

        switch_app_theme.setOnCheckedChangeListener { darkTheme, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        nsv_edit_userprofile.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY > oldScrollY) {
                    Log.i("position:", "SCROLL DOWN")
                }
                if (scrollY < oldScrollY) {
                    Log.i("position:", "SCROLL UP")
                }
                if (scrollY == 0) {
                    Log.i("position:", "TOP SCROLL")
                }
                if (scrollY == v!!.measuredHeight - v!!.getChildAt(0).measuredHeight) {
                    Log.i("position:", "BOTTOM SCROLL")
                }
            }

        })


    }


/*    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        nsv_edit_userprofile.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                println()
            }

        })
    }*/

}