package ru.surf.learn2invest.noui.cryptography

import java.security.MessageDigest


class PasswordHasher(
    private val firstName: String, private val lastName: String
) {


    /**
     * [SHA-256](https://developer.android.com/privacy-and-security/cryptography#kotlin) - хэширование, рекомендованное Google
     */
    private fun String.getSHA256Hash(): String =
        MessageDigest.getInstance("SHA-256").digest(toByteArray()).fold("") { str, it ->
            str + "%02x".format(it)
        }

    /**
     * Алгоритм генерации "Соли", которая нужна для того,
     * чтобы усложнить подбор пароля
     */
    private fun String.addSaltToMessage(): String {
        return "${firstName}${this}${lastName}"
    }

    /**
     * Функция получения hash'а пароля
     */
    fun passwordToHash(password: String): String = password.addSaltToMessage().getSHA256Hash()
}

