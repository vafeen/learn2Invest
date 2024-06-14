package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект криптовалюты портфеля?
 */

@Entity
data class AssetInvest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetID: String,
    val name: String, //Имя коина (Bitcoin)
    val symbol: String, //Абревиатура (BTC)
    val coinPrice: Float, //Цена
    val amount: Float, //Колличество
) {
    override fun toString(): String {
        return "id=$id name=$name symbol=$symbol price=$coinPrice amount=$amount"
    }
}




