package ru.surf.learn2invest.noui.database_components.entity.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект транзакции в истории свех транзакций
 * @param id [Первичный ключ в базе данных]
 * @param coinID [Идентификатор Coin'а для API]
 * @param name [Имя Coin'а]
 * @param symbol [Аббревиатура ]
 * @param coinPrice [Цена покупки]
 * @param dealPrice [Общая умма сделки (цена * количество)]
 * @param amount [Количество ]
 * @param transactionType [Тип транзакции (покупка/продажа)]
 */
@Entity
open class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val coinID: String,
    val name: String,
    val symbol: String,
    val coinPrice: Float,
    val dealPrice: Float,
    val amount: Float,
    val transactionType: TransactionsType
)
