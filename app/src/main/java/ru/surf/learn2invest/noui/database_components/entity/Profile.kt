package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Профиль пользователя со всеми настройками
 */

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val firstName: String, //Имя
    val lastName: String, //Фамилия
    val biometry: Boolean, //Вкл/Выкл входа по биометрии
    val fiatBalance: Float, //Баланс обычных денег
    val assetBalance: Float, //Суммарная стоимость активов (обновляется после покупки чего либо или продажи)
    val hash: String? = null, //Хэш пина
    val tradingPasswordHash: String? = null // Хэш Торгового пароля
)

