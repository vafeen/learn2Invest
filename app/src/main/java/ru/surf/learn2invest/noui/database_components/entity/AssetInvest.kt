package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект криптовалюты портфеля?
 */
@Entity
class AssetInvest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val symbol: String,
    val iconURL: String,
    val coinPrice: Int,
    val changePercent24Hr: Float,
    val amount: Int,
)


