package online.fatbook.fatbookapp.util

import android.app.AlertDialog
import android.content.Context
import online.fatbook.fatbookapp.R

class LoadingDialog(val context: Context) {
    private lateinit var dialog: AlertDialog
    fun startLoading() {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.loading_dialog)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun isDismiss() {
        dialog.dismiss()
    }
}