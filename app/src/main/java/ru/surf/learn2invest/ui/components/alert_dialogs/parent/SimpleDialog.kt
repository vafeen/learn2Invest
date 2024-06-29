package ru.surf.learn2invest.ui.components.alert_dialogs.parent

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import ru.surf.learn2invest.utils.getStringOrNull

/**
 * Простой диалог с title, message и кнопками с действиями
 * @param context [context диалога]
 * @param titleRes [title диалога]
 * @param messageRes [message диалога]
 * @param isCancelable [отменяемый диалог или нет]
 * @param positiveButtonTitleRes [Название позитивной кнопки]
 * @param negativeButtonTitleRes [Название негативной кнопки]
 * @param onPositiveButtonClick [событие нажатия на позитивную кнопку]
 * @param onNegativeButtonClick [событие нажатия на негативную кнопку]
 */
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
        .setTitle(titleRes?.let { context.getString(it) })
        .setMessage(messageRes?.let { context.getString(it) })
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


}