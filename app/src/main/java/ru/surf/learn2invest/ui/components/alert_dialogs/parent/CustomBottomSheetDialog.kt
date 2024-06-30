package ru.surf.learn2invest.ui.components.alert_dialogs.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Класс для удобной реализации BottomSheetDialogs
 */
abstract class CustomBottomSheetDialog() : BottomSheetDialogFragment() {
    abstract val dialogTag: String

    /**
     * Инициализация всех Listeners
     */
    protected abstract fun initListeners()

    /**
     * XML ресурс экрана
     */
    protected abstract fun getDialogView(): View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initListeners()
        return getDialogView()
    }
}