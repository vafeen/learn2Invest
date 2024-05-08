package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity




// экран истории
@Entity
open class Transaction(
    name: String,
    symbol: String,
    iconURL: String,
    coinPrice: Int,
    val dealPrice: Int,
) : Coin(
    name = name,
    symbol = symbol,
    iconURL = iconURL,
    coinPrice = coinPrice,
) {

}