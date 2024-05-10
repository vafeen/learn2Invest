package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект транзакции в истории свех транзакций
 */
@Entity
open class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    val symbol: String,
    val iconURL: String,
    val coinPrice: Int,
    val dealPrice: Int,
    val amount: Int,
)
