package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект транзакции в истории свех транзакций
 */
@Entity
open class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String = "coin", //Имя коина (Bitcoin)
    val symbol: String, //Абревиатура (BTC)
    val iconURL: String = "iconURL", //URL для закачки иконки
    val coinPrice: Int = 0, //Закупочная цена одного
    val dealPrice: Int = 0, //Общая умма сделки (цена * количество)
    val amount: Int = 0, //Количество
)
