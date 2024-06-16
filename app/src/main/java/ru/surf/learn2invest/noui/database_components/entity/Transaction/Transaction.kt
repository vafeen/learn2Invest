package ru.surf.learn2invest.noui.database_components.entity.Transaction

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект транзакции в истории свех транзакций
 */
@Entity
open class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val coinID: String, //ID коина (нужен для запросов)
    val name: String, //Имя коина (Bitcoin)
    val symbol: String, //Абревиатура (BTC)
    val coinPrice: Float, //Закупочная цена одного
    val dealPrice: Float, //Общая умма сделки (цена * количество)
    val amount: Float, //Количество
    val transactionType: TransactionsType
)
