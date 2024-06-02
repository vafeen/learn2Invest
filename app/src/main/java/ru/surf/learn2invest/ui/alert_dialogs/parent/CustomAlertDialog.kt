package ru.surf.learn2invest.ui.alert_dialogs.parent

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.Window


/**
 * Класс для удобной реализации Custom alert dialogs.
 *
 * Декларация:
 * ```
 * class AskToDeleteProfile(
 *     context: Context,
 *     layoutInflater: LayoutInflater
 * ) : CustomAlertDialog(context) {
 *
 *     private var binding =
 *         AskToDeleteProfileDialogBinding.inflate(layoutInflater)
 *
 *     override fun setCancelable(): Boolean {
 *         return true
 *     }
 *
 *     override fun initListeners() {
 *         binding.noDelete.setOnClickListener {
 *             cancel()
 *         }
 *
 *         binding.okDelete.setOnClickListener {
 *             cancel()
 *         }
 *     }
 *
 *     override fun getDialogView(): View {
 *         return binding.root
 *     }
 *
 * }
 * ```
 *
 * Использование:
 * ```
 * val dialog =
 *             AskToDeleteProfile(context = this, layoutInflater = layoutInflater).initDialog()
 *
 *         dialog.show()
 *
 *         dialog.cancel()
 *```
 *
 */
abstract class CustomAlertDialog(context: Context) {

    private var dialog: Dialog = Dialog(context)

    private var initialized: Boolean = false

    /**
     * Отменяется ли диалог при нажатии на свободную часть экрана
     */
    abstract fun setCancelable(): Boolean

    /**
     * Инициализация всех Listeners
     */
    abstract fun initListeners()

    /**
     * XML ресурс экрана
     */
    abstract fun getDialogView(): View

    /**
     * Функция инициализации всего функционала
     */
    open fun initDialog(): CustomAlertDialog {
        initialized = true

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.setBackgroundDrawable(getBackground())

        dialog.setCancelable(setCancelable())

        dialog.setContentView(getDialogView())

        initListeners()

        return this
    }

    /**
     * Смена background'a. По умолчанию - TRANSPARENT
     */
    open fun getBackground(): Drawable {
        return ColorDrawable(Color.TRANSPARENT)
    }

    fun show() {
        if (initialized) {
            dialog.show()
        } else {
            Log.e(
                "CustomAlertDialog",
                "Dialog is not initialized. Please call fun initDialog before calling fun show()"
            )
        }
    }

    fun cancel() {
        dialog.cancel()
    }
}