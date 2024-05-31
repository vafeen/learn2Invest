package ru.surf.learn2invest.network_components.util

object Const {
    const val BASE_URL = "https://api.coincap.io/v2/"
    const val API_MARKET_REVIEW = "${BASE_URL}assets?limit=2000"
    const val API_HISTORY = "${BASE_URL}assets/{id}/history"
    const val WEEK: Long = 604800000 // миллисекунды
    const val INTERVAL: String = "h12" // интервал выборки данных. Доступные форматы m1, m5, m15, m30, h1, h2, h6, h12, d1
}