package ru.surf.learn2invest.ui.tests.data

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
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

fun insertProfileInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {
        Learn2InvestApp.mainDB.profileDao().insertAll(testProfile)
    }
}