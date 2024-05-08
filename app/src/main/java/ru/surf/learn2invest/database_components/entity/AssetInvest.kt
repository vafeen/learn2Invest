package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity



@Entity
class AssetInvest(
    // экран мои инвестиции
    name: String,
    symbol: String,
    iconURL: String,
    coinPrice: Int,
    dealPrice: Int,
    amount: Int,
) : TransactionCoinSpecific(
    name = name,
    symbol = symbol,
    iconURL = iconURL,
    coinPrice = coinPrice,
    dealPrice = dealPrice,
    amount = amount
) {

}


