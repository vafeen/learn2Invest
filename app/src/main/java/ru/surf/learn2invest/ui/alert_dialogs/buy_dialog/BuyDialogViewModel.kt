package ru.surf.learn2invest.ui.alert_dialogs.buy_dialog

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest

class BuyDialogViewModel : ViewModel() {
    lateinit var realTimeUpdateJob: Job
    var haveAssetsOrNot = false
    lateinit var coin: AssetInvest
}