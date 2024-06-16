package ru.surf.learn2invest.ui.alert_dialogs.reset_stats

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

fun resetStats(lifecycleScope: LifecycleCoroutineScope) {
//    AssetBalanceHistory::class,
//    AssetInvest::class,
//    Profile::class,
//    SearchedCoin::class,
//    Transaction::class,
    val profile = App.profile.copy(
        fiatBalance = 0f,
        assetBalance = 0f
    )

    lifecycleScope.launch(Dispatchers.IO) {
        DatabaseRepository.apply {
            clearAllTables()

            insertAllProfile(
                profile
            )
        }
    }
}