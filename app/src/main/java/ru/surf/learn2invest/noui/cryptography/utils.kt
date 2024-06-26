package ru.surf.learn2invest.noui.cryptography

import android.content.Context
import androidx.biometric.BiometricManager
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Profile


/**
 * Метод верификации пользовательского пароля
 */
fun verifyPIN(user: Profile, password: String): Boolean {
    return PasswordHasher(
        firstName = user.firstName,
        lastName = user.lastName
    ).passwordToHash(password = password) == user.hash
}
fun String.isTrueTradingPasswordOrIsNotDefined(): Boolean {
    return if (DatabaseRepository.profile.tradingPasswordHash != null) {
        verifyTradingPassword(user = DatabaseRepository.profile, password = this)
    } else true
}
/**
 * Метод верификации торгового пароля
 */
fun verifyTradingPassword(user: Profile, password: String): Boolean {
    return PasswordHasher(
        firstName = user.firstName,
        lastName = user.lastName
    ).passwordToHash(password = password) == user.tradingPasswordHash
}


/**
 * Проверка наличия биометрического аппаратного обеспечения
 */
fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> false
    }
}

