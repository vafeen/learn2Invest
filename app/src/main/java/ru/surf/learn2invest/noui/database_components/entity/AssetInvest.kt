package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект криптовалюты портфеля?
 */

@Entity
class AssetInvest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, //Имя коина (Bitcoin)
    val symbol: String, //Абревиатура (BTC)
    val coinPrice: Float, //Цена
    val amount: Float, //Колличество
)




