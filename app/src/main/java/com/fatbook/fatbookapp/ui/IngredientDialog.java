package com.fatbook.fatbookapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Spinner;

import com.fatbook.fatbookapp.R;

public class IngredientDialog {

    public void showDialog(Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        //dialog.setContentView(R.layout.dialog_ingredient_quantity);


    }

}
