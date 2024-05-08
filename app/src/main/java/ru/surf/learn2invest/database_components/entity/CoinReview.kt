package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity


// экран обзор рынка
@Entity
class CoinReview(
    name: String,
    symbol: String,
    iconURL: String,
    coinPrice: Int,
    val changePercent24Hr: Float
) : Coin(
    name = name,
    symbol = symbol,
    iconURL = iconURL,
    coinPrice = coinPrice
) {

}
