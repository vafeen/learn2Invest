package ru.surf.learn2invest.network_components.responses

data class CoinPriceResponse(
    val priceUsd: Double, // цена
    val time: Long,
    val date: String
)
