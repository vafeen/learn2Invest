package ru.surf.learn2invest.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
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

/**
 * Метод проверки, задан ли торговый пароль и его верификации
 */
fun String.isTrueTradingPasswordOrIsNotDefined(profile: Profile): Boolean {
    return if (profile.tradingPasswordHash != null) {
        verifyTradingPassword(user = profile, password = this)
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
fun isBiometricAvailable(activity: AppCompatActivity): Boolean {
    return BiometricManager.from(activity)
        .canAuthenticate(
            Authenticators.BIOMETRIC_STRONG or
                    Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
}

