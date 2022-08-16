/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package online.fatbook.fatbookapp.util

/**
 * Various extension functions for AppCompatActivity.
 */

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*

fun <T : ViewModel> FragmentActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProvider(this)[viewModelClass]

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProvider(activity!!)[viewModelClass]

fun <T : ViewModel> Fragment.obtainFragmentViewModel(viewModelClass: Class<T>) =
    ViewModelProvider(this)[viewModelClass]

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun Fragment.commitChildFragment(containerId: Int, fragment: Fragment) {
    childFragmentManager.beginTransaction()
        .replace(containerId, fragment, fragment.javaClass.simpleName)
        .addToBackStack(fragment.javaClass.simpleName)
        .commit()
}

fun Fragment.removeChildFragment(fragment: Fragment) {
    childFragmentManager.beginTransaction()
        .remove(fragment)
        .commit()
}

fun FragmentActivity.commitFragment(containerId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment, fragment.javaClass.simpleName)
        .addToBackStack(fragment.javaClass.simpleName)
        .commit()
}

fun Fragment.hideKeyboard() {
    context?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
    }
}

fun Fragment.hideKeyboard(editText: EditText) {
    context?.let {
        editText.clearFocus()
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
    }
}

fun Fragment.showKeyboard(editText: EditText) {
    context?.let {
        editText.requestFocus()
        editText.setSelection(editText.length())
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window?.decorView?.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun AlertDialog.notClosingPositiveButtonClick(clickListener: DialogInterface.OnClickListener) {
    notClosingButtonClick(AlertDialog.BUTTON_POSITIVE, clickListener)
}

fun Context.applicationName(): String {
    val applicationInfo = applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(
        stringId
    )
}

private fun AlertDialog.notClosingButtonClick(
    identifier: Int,
    clickListener: DialogInterface.OnClickListener
) {
    window?.decorView?.post {
        getButton(identifier).setOnClickListener {
            clickListener.onClick(this, identifier)
        }
    }
}