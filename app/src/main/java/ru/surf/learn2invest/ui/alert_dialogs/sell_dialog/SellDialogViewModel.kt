package ru.surf.learn2invest.ui.alert_dialogs.sell_dialog

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest

class SellDialogViewModel : ViewModel() {
    lateinit var realTimeUpdateJob: Job
    lateinit var coin: AssetInvest
}