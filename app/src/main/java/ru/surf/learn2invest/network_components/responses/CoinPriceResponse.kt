package ru.surf.learn2invest.network_components.responses

data class CoinPriceResponse(
    val priceUsd: Float, // цена
    val time: Long,
    val date: String
)
