package ru.surf.learn2invest.ui.components.alert_dialogs.parent

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ru.surf.learn2invest.utils.getStringOrNull

class SimpleDialog(
    context: Context,
    @StringRes titleRes: Int? = null,
    @StringRes messageRes: Int? = null,
    isCancelable: Boolean,
    @StringRes positiveButtonTitleRes: Int?,
    @StringRes negativeButtonTitleRes: Int?,
    onPositiveButtonClick: () -> Unit = {},
    onNegativeButtonClick: () -> Unit = {}
) {
    private var alert: AlertDialog = AlertDialog.Builder(context)
        .setTitle(titleRes?.let { ContextCompat.getString(context, it) })
        .setMessage(messageRes?.let { ContextCompat.getString(context, it) })
        .setCancelable(isCancelable)
        .apply {
            context.getStringOrNull(positiveButtonTitleRes)?.let {
                setPositiveButton(it) { _: DialogInterface, _: Int -> onPositiveButtonClick() }
            }
            context.getStringOrNull(negativeButtonTitleRes)?.let {
                setNegativeButton(it) { _: DialogInterface, _: Int -> onNegativeButtonClick() }
            }
        }.create()

    fun show() {
        alert.show()
    }

    fun cancel() {
        alert.cancel()
    }
    /**
     * Shows simple dialog with title, message, buttons (each button optionally) using string
     * @param title dialog's title string
     * @param message dialog's message string
     * @param isCancelable dialog's cancelable property
     * @param positiveButtonTitle dialog's positive button title (if null positive button will not be shown)
     * @param negativeButtonTitle dialog's negative button title (if null negative button will not be shown)
     * @param neutralButtonTitle dialog's neutral button title (if null neutral button will not be shown)
     * @param onClickListener is [DialogInterface.OnClickListener] or null
     */

    /**
     * Shows simple dialog with title, message, buttons (each button optionally) using string resources
     * @param titleRes dialog's title string resource
     * @param messageRes dialog's message string resource
     * @param isCancelable dialog's cancelable property
     * @param positiveButtonTitleRes dialog's positive button title (if null positive button will not be shown)
     * @param negativeButtonTitleRes dialog's negative button title (if null negative button will not be shown)
     * @param neutralButtonTitleRes dialog's neutral button title (if null neutral button will not be shown)
     * @param onClickListener is [DialogInterface.OnClickListener] or null
     */
}