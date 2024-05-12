package ru.surf.learn2invest.noui.cryptography

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
        return "${user.firstName}\\\\$this////${user.lastName}"
    }


    private fun passwordToHash(password: String): String {
        return password.addSaltToMessage().getSHA256Hash()
    }


    /**
     * Метод верификации(проверки) пользовательского пароля
     */
    fun verify(password: String): Boolean {

        return passwordToHash(password = password) == user.hash
    }

}