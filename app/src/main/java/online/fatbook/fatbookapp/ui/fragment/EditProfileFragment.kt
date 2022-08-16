package online.fatbook.fatbookapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.util.LoadingDialog
import online.fatbook.fatbookapp.util.hideKeyboard

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
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        Log.i("view:", "onCreateView")

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("view:", "onViewCreated")

//        edittext_profile_bio.filters = arrayOf<InputFilter>(MyTextFilter())

        edittext_profile_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == edittext_profile_title.filters.filterIsInstance<InputFilter.LengthFilter>()
                        .firstOrNull()?.max!!
                ) {
                    hideKeyboard(edittext_profile_title)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

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
            edittext_profile_title.setText(
                edittext_profile_title.text.toString().replace("\\s+".toRegex(), " ")
            )
            edittext_profile_bio.setText(
                edittext_profile_bio.text.toString().replace("\\s+".toRegex(), " ")
            )
            hideKeyboard(edittext_profile_bio)
        }

//        val loadingDialog = LoadingDialog(requireContext())

//        loadingDialog.startLoading()
        progressbarLayout.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                progressbarLayout.visibility = View.GONE
//                loadingDialog.isDismiss()
            }

        }, 1500)

    }
}
