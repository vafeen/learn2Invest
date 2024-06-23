package ru.surf.learn2invest.ui.tests.data

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App.Companion.profile
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Profile

val testProfile = Profile(
    id = 0,
    firstName = "A",
    lastName = "Vafeen",
    biometry = true,
    fiatBalance = 0f,
    assetBalance = 0f,

    ).copy(
    hash = PasswordHasher(
        firstName = profile.firstName,
        lastName = profile.lastName
    ).passwordToHash("0000"),
//        tradingPasswordHash = PasswordHasher(user = it).passwordToHash("1235789"))
)

fun insertProfileInCoroutineScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch(Dispatchers.IO) {
        DatabaseRepository.insertAllProfile(testProfile)
    }
}