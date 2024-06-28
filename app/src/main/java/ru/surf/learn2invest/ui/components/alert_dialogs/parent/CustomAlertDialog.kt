package ru.surf.learn2invest.ui.components.alert_dialogs.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * Класс для удобной реализации Custom alert dialogs.
 * @param supportFragmentManager [Менеджер открытия диалогов]
 */
abstract class CustomAlertDialog(private val supportFragmentManager: FragmentManager) :
    DialogFragment() {
    protected abstract val dialogTag: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initListeners()
        isCancelable = setCancelable()
        return getDialogView()
    }

    /**
     * Отменяется ли диалог при нажатии на свободную часть экрана
     */
    protected open fun setCancelable(): Boolean = true

    /**
     * Инициализация всех Listeners
     */
    protected abstract fun initListeners()

    /**
     * XML ресурс экрана
     */
    protected abstract fun getDialogView(): View

    open fun show() {
        show(supportFragmentManager, dialogTag)
    }

    open fun cancel() {
        dismiss()
    }
}