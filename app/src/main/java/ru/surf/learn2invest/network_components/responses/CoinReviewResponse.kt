package ru.surf.learn2invest.network_components.responses

data class CoinReviewResponse(
    val id: String, //ID название
    val rank: Int, //ранг
    val symbol: String, //Абревиатура
    val name: String, //Название
    val supply: Double,
    val maxSupply: Double,
    val marketCapUsd: Double, //Капитализация
    val volumeUsd24Hr: Double,
    val priceUsd: Double, //Цена
    val changePercent24Hr: Double, //Изменение цены за последни 24 часа
    val vwap24Hr: Double
)
