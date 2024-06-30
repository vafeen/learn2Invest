package ru.surf.learn2invest.utils

import ru.surf.learn2invest.noui.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewDto

/**
 * Docs?
 */
object RetrofitLinks {
    const val BASE_URL = "https://api.coincap.io/v2/"
    const val API_MARKET_REVIEW = "assets?limit=2000"
    const val API_HISTORY = "assets/{id}/history"
    const val API_COIN_REVIEW = "assets/{id}"
    const val API_ICON = "https://cryptofonts.com/img/icons/"
    const val WEEK: Long = 604800000 // миллисекунды
    const val INTERVAL: String =
        "d1" // интервал выборки данных. Доступные форматы m1, m5, m15, m30, h1, h2, h6, h12, d1
}

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