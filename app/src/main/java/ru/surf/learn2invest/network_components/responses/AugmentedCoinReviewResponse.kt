package ru.surf.learn2invest.network_components.responses

data class AugmentedCoinReviewResponse(
    val id: String, //название (типа id)
    val rank: Int, //Ранг
    val symbol: String, //Абревиатура
    val name: String, //Название
    val supply: Float,
    val maxSupply: Float,
    val marketCapUsd: Float, //Капитализация
    val volumeUsd24Hr: Float,
    val priceUsd: Float, //Цена
    val changePercent24Hr: Float, //Процент изменения цены за 24 часа
    val vwap24Hr: Float,
    val explorer: String
)