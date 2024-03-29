package cz.muni.moviewishlist.main

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.View
import cz.muni.moviewishlist.R

class Methods {

    companion object {
        /**
         * Creates and shows a given dialog.
         */
        fun createDialog(
            context: Context, view: View, @StringRes titleId: Int, @StringRes addId: Int = R.string.add_button,
            createdListener: () -> Unit = {},
            negativeListener: ((dialog:DialogInterface, which:Int) -> Unit) = { _, _ -> },
            positiveListener: ((dialog:DialogInterface, which:Int) -> Unit)
        ) {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(titleId)
            createdListener.invoke()
            dialog.setView(view)
            dialog.setPositiveButton(context.getString(addId), positiveListener)
            dialog.setNegativeButton(context.getString(R.string.cancel_button), negativeListener)
            dialog.show()
        }

    }
}