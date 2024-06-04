package ru.surf.learn2invest.noui.cryptography

import android.util.Log
import ru.surf.learn2invest.noui.database_components.entity.Profile
import java.security.MessageDigest


class PasswordHasher(
    val user: Profile
) {


    /**
     * [SHA-256](https://developer.android.com/privacy-and-security/cryptography#kotlin) - хэширование, рекомендованное Google
     */
    private fun String.getSHA256Hash(): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(toByteArray())
            .fold("") { str, it ->
                str + "%02x".format(it)
            }

    /**
     * Алгоритм генерации "Соли", которая нужна для того,
     * чтобы усложнить подбор пароля
     */
    private fun String.addSaltToMessage(): String {
        return "${user.firstName}${this}${user.lastName}"
    }

    /**
     * Функция получения hash'а пароля
     */
    fun passwordToHash(password: String): String {
        Log.d("pasword", "password:${password} = ${password.addSaltToMessage().getSHA256Hash()}")
        return password.addSaltToMessage().getSHA256Hash()
    }


    /**
     * Метод верификации(проверки) пользовательского пароля
     */
    fun verifyPIN(password: String): Boolean {

        return passwordToHash(password = password) == user.hash
    }

    fun verifyTradingPassword(password: String): Boolean {
        return passwordToHash(password = password) == user.tradingPasswordHash
    }
}