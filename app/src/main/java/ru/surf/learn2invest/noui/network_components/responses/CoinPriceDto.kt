package ru.surf.learn2invest.noui.network_components.responses

/**
 * Docs?
 */
data class CoinPriceDto(
    val priceUsd: Float, // цена
    val time: Long,
    val date: String
)
