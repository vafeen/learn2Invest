package ru.surf.learn2invest.noui.network_components.responses

/**
 * Docs?
 */
data class CoinReviewDto(
    val id: String, //ID название
    val rank: Int, //ранг
    val symbol: String, //Абревиатура
    val name: String, //Название
    val supply: Float,
    val maxSupply: Float,
    val marketCapUsd: Float, //Капитализация
    val volumeUsd24Hr: Float,
    val priceUsd: Float, //Цена
    val changePercent24Hr: Float, //Изменение цены за последни 24 часа
    val vwap24Hr: Float
)
