package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity





// экран мои инвестиции
@Entity
open class TransactionCoinSpecific(
    name: String,
    symbol: String,
    iconURL: String,
    coinPrice: Int,
    dealPrice: Int,
    val amount: Int,
) : Transaction(
    name = name,
    symbol = symbol,
    iconURL = iconURL,
    coinPrice = coinPrice,
    dealPrice = dealPrice
)
