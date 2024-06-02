package ru.surf.learn2invest.ui.tests.data

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.Learn2InvestApp
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.database_components.entity.Profile

val testProfile = Profile(
    id = 0,
    firstName = "A",
    lastName = "Vafeen",
    pin = 0,
    notification = true,
    biometry = true,
    confirmDeal = true,
    fiatBalance = 0,
    assetBalance = 0,
).let {
    it.copy(hash = PasswordHasher(user = it).passwordToHash("0000"))
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

fun insertProfileInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {
        Learn2InvestApp.mainDB.profileDao().insertAll(testProfile)
    }
}

fun insertAlertInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {

        Learn2InvestApp.mainDB.priceAlertDao().insertAll(
            *testAlerts.toTypedArray()
        )

//        //Loher.d("Закинуто")
    }
}