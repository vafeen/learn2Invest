package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Объект криптовалюты портфеля
 * @param id [Первичный ключ в базе данных]
 * @param assetID [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 * @param coinPrice [Цена ]
 * @param amount [Колличество ]
 */
@Entity
data class AssetInvest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetID: String,
    val name: String,
    val symbol: String,
    val coinPrice: Float,
    val amount: Float,
) {
    override fun toString(): String {
        return "id=$id name=$name symbol=$symbol price=$coinPrice amount=$amount"
    }
}




