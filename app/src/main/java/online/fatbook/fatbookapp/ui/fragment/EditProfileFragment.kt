package online.fatbook.fatbookapp.ui.fragment

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_test.*
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.util.MyTextFilter
import online.fatbook.fatbookapp.util.hideKeyboard
import java.lang.Math.hypot
import kotlin.math.hypot
import kotlin.properties.Delegates

class EditProfileFragment : Fragment() {
    private var binding: FragmentEditProfileBinding? = null
    private var savedScrollX: Int = 0
    private var savedScrollY: Int = 0

    private var bioTextLength: Int = 0
    private var strBio: String? = null
    private var strTmp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("view:", "onCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        Log.i("view:", "onCreateView")

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("view:", "onViewCreated")

//        edittext_profile_bio.filters = arrayOf<InputFilter>(MyTextFilter())

        edittext_profile_bio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bioTextLength =
                    edittext_profile_bio.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                bioTextLength -= s.toString().length
                textview_bio_length.text = bioTextLength.toString()
                if (bioTextLength == 0) {
                    hideKeyboard(edittext_profile_bio)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        button_save_edit_userprofile.setOnClickListener {
            edittext_profile_bio.setText(edittext_profile_bio.text.toString().replace("\\s+".toRegex(), " "))
        }

    }
    /*if (savedScrollY != 0) {
        nsv_edit_userprofile.post(Runnable { nsv_edit_userprofile.scrollTo(0, savedScrollY) })
        Log.d("position:", "$savedScrollY")
    }
*/

/*
        focusOnView()
*/

/*
        nsv_edit_userprofile.scrollTo(3, 30)
*/

    /* val value = if (savedInstanceState?.getInt("scroll", 0) != null) {
         savedInstanceState.getInt("scroll", 0)
     } else {
         0
     }

     Log.d("scroll", "$value LOADED")
     nsv_edit_userprofile.smoothScrollTo(value, value)*/


    /*val loadingDialog = LoadingDialog(requireContext())

    loadingDialog.startLoading()
    val handler = Handler()
    handler.postDelayed(object : Runnable {
        override fun run() {
            loadingDialog.isDismiss()
        }

    }, 5000)
*/
    /* nsv_edit_userprofile.setOnScrollChangeListener(object :
         NestedScrollView.OnScrollChangeListener {
         override fun onScrollChange(
             v: NestedScrollView?,
             scrollX: Int,
             scrollY: Int,
             oldScrollX: Int,
             oldScrollY: Int
         ) {
             savedScrollY = scrollY

              if (scrollY > oldScrollY) {
                  Log.i("position:", "SCROLL DOWN")
              }
              if (scrollY < oldScrollY) {
                  Log.i("position:", "SCROLL UP")
              }
              if (scrollY == 0) {
                  Log.i("position:", "TOP SCROLL")
              }
              if (scrollY == v!!.measuredHeight - v.getChildAt(0).measuredHeight) {
                  Log.i("position:", "BOTTOM SCROLL")
              }
         }

     })*/

}
/*
override fun onDestroyView() {
    super.onDestroyView()
    Log.i("view:", "onDestroyView")

    savedScrollY = nsv_edit_userprofile.scrollY
}*/
/* private fun setTheme(night: Boolean) {
     if (imageview_anim.isVisible) {
         return
     }

     val w = container.width
     println("$w")
     val h = container.height
     println("$h")


     val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
     val canvas = Canvas(bitmap)
     container.draw(canvas)

     imageview_anim.setImageBitmap(bitmap)
     imageview_anim.isVisible = true

     val finalRadius = hypot(w.toFloat(), h.toFloat())

     if(night) {
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
     } else {
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
     }

     val anim = ViewAnimationUtils.createCircularReveal(container, w / 2, h / 2, 0f, finalRadius)
     anim.duration = 400L
     anim.doOnEnd {
         imageview_anim.setImageDrawable(null)
         imageview_anim.isVisible = false
     }
     anim.start()
 }*/


/*private fun focusOnView() {
    nsv_edit_userprofile.post(Runnable {
        nsv_edit_userprofile.scrollTo(
            0,
            switch_app_theme.getBottom()
        )
    })
}*/

/*override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("scroll", savedScrollY)
    Log.d("scroll", "$savedScrollY SAVED")

    Log.i("view:", "onSaveInstanceState")

}

override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    Log.i("view:", "onViewStateRestored")

}

override fun onStart() {
    super.onStart()
    Log.i("view:", "onStart")

}

override fun onStop() {
    super.onStop()
    Log.i("view:", "onStop")

}



override fun onDestroy() {
    super.onDestroy()
    Log.i("view:", "onDestroy")

}*/

