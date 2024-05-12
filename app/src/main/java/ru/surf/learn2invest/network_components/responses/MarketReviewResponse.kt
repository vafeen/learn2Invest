package ru.surf.learn2invest.network_components.responses

data class MarketReviewResponse(
    val data: List<CoinReviewResponse>,
    val timestamp: Long
)