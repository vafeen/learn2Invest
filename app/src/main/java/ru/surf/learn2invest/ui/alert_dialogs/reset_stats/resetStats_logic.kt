package ru.surf.learn2invest.ui.alert_dialogs.reset_stats

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App

fun resetStats(lifecycleScope: LifecycleCoroutineScope) {
//    AssetBalanceHistory::class,
//    AssetInvest::class,
//    Profile::class,
//    SearchedCoin::class,
//    Transaction::class,
    val profile = App.profile.copy(
        fiatBalance = 50000f
    )

    lifecycleScope.launch(Dispatchers.IO) {
        App.mainDB.apply {
            clearAllTables()

            profileDao().insertAll(
                profile
            )
        }
    }
}