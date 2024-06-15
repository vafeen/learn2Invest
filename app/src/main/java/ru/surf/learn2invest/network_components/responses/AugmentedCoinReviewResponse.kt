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

fun AugmentedCoinReviewResponse.toCoinReviewDto() = CoinReviewDto(
    id = this.id,
    rank = this.rank,
    symbol = this.symbol,
    name = this.name,
    supply = this.supply,
    maxSupply = this.maxSupply,
    marketCapUsd = this.marketCapUsd,
    volumeUsd24Hr = this.volumeUsd24Hr,
    priceUsd = this.priceUsd,
    changePercent24Hr = this.changePercent24Hr,
    vwap24Hr = this.vwap24Hr
)