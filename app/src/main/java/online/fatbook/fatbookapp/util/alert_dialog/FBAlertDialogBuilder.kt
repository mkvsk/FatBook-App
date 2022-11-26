package online.fatbook.fatbookapp.util.alert_dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import online.fatbook.fatbookapp.R

object FBAlertDialogBuilder {

    private lateinit var inflater: LayoutInflater
    private lateinit var context: Context

    private lateinit var dialogView: View
    private lateinit var dialog: AlertDialog

    fun set(context: Context, inflater: LayoutInflater) {
        this.context = context
        this.inflater = inflater
    }

    fun getDialogWithPositiveAndNegativeButtons(
        title: String,
        msg: String,
        positiveBtnListener: FBAlertDialogListener,
        negativeBtnListener: FBAlertDialogListener,
        positiveBtnText: String = context.resources.getString(R.string.alert_dialog_btn_yes),
        negativeBtnText: String = context.resources.getString(R.string.alert_dialog_btn_cancel)
    ): AlertDialog {
        dialogView = inflater.inflate(R.layout.alert_dialog_layout, null)
        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_msg).text = msg
        dialog = AlertDialog.Builder(context).setView(dialogView)
            .setPositiveButton(positiveBtnText) { dialogInterface: DialogInterface, _: Int ->
                positiveBtnListener.onClick(dialogInterface)
            }
            .setNegativeButton(negativeBtnText) { dialogInterface: DialogInterface, _: Int ->
                negativeBtnListener.onClick(dialogInterface)
            }
            .create()
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.tpd_bgr))
        return dialog
    }

    fun getDialogWithPositiveButton(
        title: String,
        msg: String,
        listener: FBAlertDialogListener,
        positiveBtnText: String = context.resources.getString(R.string.alert_dialog_btn_yes),
    ): AlertDialog {
        dialogView = inflater.inflate(R.layout.alert_dialog_layout, null)
        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_msg).text = msg
        dialog = AlertDialog.Builder(context).setView(dialogView)
            .setPositiveButton(positiveBtnText) { dialogInterface: DialogInterface, _: Int ->
                listener.onClick(dialogInterface)
            }
            .create()
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.tpd_bgr))
        return dialog
    }

    fun getAppInfoDialog(listener: FBAlertDialogListener): AlertDialog {
        dialogView = inflater.inflate(R.layout.dialog_app_info, null)
        dialog = AlertDialog.Builder(context).setView(dialogView)
            .setPositiveButton("nice") { dialogInterface: DialogInterface, _: Int ->
                listener.onClick(dialogInterface)
            }
            .create()
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.tpd_bgr))
        return dialog
    }
}
