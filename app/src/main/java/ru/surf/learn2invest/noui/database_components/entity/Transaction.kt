package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект транзакции в истории свех транзакций
 */
@Entity
open class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String, //Имя коина (Bitcoin)
    val symbol: String, //Абревиатура (BTC)
    val coinPrice: Double, //Закупочная цена одного
    val dealPrice: Double, //Общая умма сделки (цена * количество)
    val amount: Int, //Количество
    val transactionType: TransactionsType
)
