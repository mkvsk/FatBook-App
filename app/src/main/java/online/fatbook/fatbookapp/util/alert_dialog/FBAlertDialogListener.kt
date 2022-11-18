package online.fatbook.fatbookapp.util.alert_dialog

import android.content.DialogInterface

interface FBAlertDialogListener {
    //    fun onPositiveBtnClick(dialogInterface: DialogInterface)
//    fun onNegativeBtnClick(dialogInterface: DialogInterface)
    fun onClick(dialogInterface: DialogInterface)
}