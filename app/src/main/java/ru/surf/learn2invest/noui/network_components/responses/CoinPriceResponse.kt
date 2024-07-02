package ru.surf.learn2invest.noui.network_components.responses

/**
 * Структура JSON для парсинга истории изменения актива
 */
data class CoinPriceResponse(
    val priceUsd: Float,
    val time: Long,
    val date: String
)
