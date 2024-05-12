package ru.surf.learn2invest.network_components.responses

data class CoinHistoryResponse(
    val data: List<CoinPriceResponse>,
    val timestamp: Long
)