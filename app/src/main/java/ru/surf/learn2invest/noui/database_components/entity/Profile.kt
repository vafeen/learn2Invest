package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Профиль пользователя со всеми настройками
 */
@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val firstName: String,
    val lastName: String,
    val pin: Int,
    val notification: Boolean,
    val biometry: Boolean,
    val confirmDeal: Boolean,
    val fiatBalance: Int,
    val assetBalance: Int,
    val hash: String? = null,
)