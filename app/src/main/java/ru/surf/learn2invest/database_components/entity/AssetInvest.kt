package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class AssetInvest(
    // экран мои инвестиции
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val symbol: String,
    val iconURL: String,
    val coinPrice: Int,
    val dealPrice: Int,
    val amount: Int,
) {
}


