package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey





// профиль пользователя со всеми настройками
@Entity
class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val pin: Int,
    val notification: Boolean,
    val biometry: Boolean,
    val confirmDeal: Boolean,
    val fiatBalance: Int,
    val assetBalance: Int
) {

}