package ru.surf.learn2invest.ui.alert_dialogs.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


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
 *     override val dialogTag: String = "exampleTag"
 *
 *     override fun setCancelable(): Boolean = false
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
 *     override fun getDialogView(): View = binding.root
 *
 * }
 * ```
 *
 * Использование:
 * ```
 * val dialog = AskToDeleteProfile(
 *                  val dialogContext: Context,
 *                  supportFragmentManager: FragmentManager
 *                  context = this,
 *                  supportFragmentManager = supportFragmentManager)
 *
 *         dialog.show()
 *
 *         dialog.cancel()
 *```
 *
 */
abstract class CustomAlertDialog(private val supportFragmentManager: FragmentManager) :
    DialogFragment() {
    protected abstract val dialogTag: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initListeners()
        this.isCancelable = setCancelable()
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
        this.show(supportFragmentManager, dialogTag)
    }

    open fun cancel() {
        this.dismiss()
    }
}