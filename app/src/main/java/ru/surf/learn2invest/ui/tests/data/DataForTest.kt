package ru.surf.learn2invest.ui.tests.data

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.database_components.entity.Profile

val testProfile = Profile(
    id = 0,
    firstName = "A",
    lastName = "Vafeen",
    notification = true,
    biometry = true,
    confirmDeal = true,
    fiatBalance = 0,
    assetBalance = 0,

    ).let {
    it.copy(
        hash = PasswordHasher(user = it).passwordToHash("0000"),
//        tradingPasswordHash = PasswordHasher(user = it).passwordToHash("1235789"))
    )
}
val testAlerts = listOf(
    PriceAlert(
        symbol = "symbol1",
        coinPrice = 100f,
        changePercent24Hr = -1f,
        comment = "comment1"
    ),
    PriceAlert(
        symbol = "symbol2",
        coinPrice = 200f,
        changePercent24Hr = -2f,
        comment = "comment2"
    ),
    PriceAlert(
        symbol = "symbol3",
        coinPrice = 300f,
        changePercent24Hr = -3f,
        comment = "comment3"
    )
)

val testPortfolioChart = listOf(
    AssetBalanceHistory(assetBalance = 5f),
    AssetBalanceHistory(assetBalance = 4f),
    AssetBalanceHistory(assetBalance = 7f),
    AssetBalanceHistory(assetBalance = 8f),
    AssetBalanceHistory(assetBalance = 10f),
    AssetBalanceHistory(assetBalance = 7f),
    AssetBalanceHistory(assetBalance = 3f)
)

fun insertProfileInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {
        App.mainDB.profileDao().insertAll(testProfile)
    }
}

fun insertAlertInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {

        App.mainDB.priceAlertDao().insertAll(
            *testAlerts.toTypedArray()
        )

//        //Loher.d("Закинуто")
    }
}

fun insertPortfolioChartInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {
        App.mainDB.assetBalanceHistoryDao().insertAll(*testPortfolioChart.toTypedArray())
    }
}