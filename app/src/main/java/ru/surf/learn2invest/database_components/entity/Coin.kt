package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// объект крипты
@Entity
open class Coin(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    val symbol: String,
    val iconURL: String,
    val coinPrice: Int
) {
}




