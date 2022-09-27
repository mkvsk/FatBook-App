package online.fatbook.fatbookapp.ui.fragment.user

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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.callback.ResultCallback
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.databinding.FragmentEditProfileBinding
import online.fatbook.fatbookapp.ui.viewmodel.UserViewModel
import online.fatbook.fatbookapp.util.hideKeyboard
import online.fatbook.fatbookapp.util.obtainViewModel
import org.apache.commons.lang3.StringUtils

class EditUserProfileFragment : Fragment() {
    private var binding: FragmentEditProfileBinding? = null
    private var savedScrollX: Int = 0
    private var savedScrollY: Int = 0

    private var bioTextLength: Int = 0
    private var strBio: String? = null
    private var strTmp: String? = null

    private val userViewModel by lazy { obtainViewModel(UserViewModel::class.java) }

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
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData(userViewModel.user.value!!.username!!)


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
                button_save_edit_userprofile.visibility = View.VISIBLE
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
        progressbarLayout_edit_userprofile.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            progressbarLayout_edit_userprofile.visibility = View.GONE
            //                loadingDialog.isDismiss()
        }, 1500)

    }

    private fun loadData(username: String) {
        userViewModel.getUserByUsername(username, object : ResultCallback<User> {
            override fun onResult(value: User?) {
                value?.let {
                        userViewModel.user.value = value
                        drawData(userViewModel.user.value!!)
                }
            }

            override fun onFailure(value: User?) {

            }
        })
    }

    private fun drawData(user: User) {
        if (!user.title.isNullOrEmpty()) {
            edittext_profile_title.setText(user.title)
        } else {
            edittext_profile_title.setText(StringUtils.EMPTY)
        }

        if (!user.website.isNullOrEmpty()) {
            edittext_profile_website.setText(user.website)
        } else {
            edittext_profile_website.setText(StringUtils.EMPTY)
        }

        if (!user.bio.isNullOrEmpty()) {
            edittext_profile_bio.setText(user.bio)
        } else {
            edittext_profile_bio.setText(StringUtils.EMPTY)
        }

        if (!user.profileImage.isNullOrEmpty()) {
            Glide.with(requireContext()).load(user.profileImage)
                .into(imageview_userphoto_edit_userprofile)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_default_userphoto)
                .into(imageview_userphoto_edit_userprofile)
        }
    }
}
