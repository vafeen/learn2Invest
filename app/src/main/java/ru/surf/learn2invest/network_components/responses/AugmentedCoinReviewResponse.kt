package ru.surf.learn2invest.network_components.responses

data class AugmentedCoinReviewResponse(
    val id: String, //название (типа id)
    val rank: Int, //Ранг
    val symbol: String, //Абревиатура
    val name: String, //Название
    val supply: Double,
    val maxSupply: Double,
    val marketCapUsd: Double, //Капитализация
    val volumeUsd24Hr: Double,
    val priceUsd: Double, //Цена
    val changePercent24Hr: Double, //Процент изменения цены за 24 часа
    val vwap24Hr: Double,
    val explorer: String
)