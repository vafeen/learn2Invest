package ru.surf.learn2invest.noui.network_components

import android.util.Log
import ru.surf.learn2invest.noui.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.noui.network_components.responses.CoinPriceDto
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewDto
import ru.surf.learn2invest.noui.network_components.responses.ResponseWrapper
import ru.surf.learn2invest.noui.network_components.util.CoinRetrofitClient
import ru.surf.learn2invest.noui.network_components.util.Const

/**
 * Docs?
 */
object NetworkRepository {
    private val coinAPIService = CoinRetrofitClient.client.create(
        CoinAPIService::class.java
    )

    suspend fun getMarketReview(): ResponseWrapper<List<CoinReviewDto>> =
        try {
            val response = coinAPIService.getMarketReview()
            Log.d("RETROFIT", response.toString())
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            ResponseWrapper.NetworkError
        }


    /**
     * выводит историю цен коина за предыдущие 6 дней, не считая сегодня
     * id - название коина
     * например bitcoin
     **/
    suspend fun getCoinHistory(id: String): ResponseWrapper<List<CoinPriceDto>> =
        try {
            val response = coinAPIService.getCoinHistory(
                id = id.lowercase(),
                interval = Const.INTERVAL,
                start = System.currentTimeMillis() - Const.WEEK,
                end = System.currentTimeMillis()
            )
            Log.d("RETROFIT", response.toString())
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            ResponseWrapper.NetworkError
        }


    suspend fun getCoinReview(id: String): ResponseWrapper<AugmentedCoinReviewResponse> =
        try {
            val response = coinAPIService.getCoinReview(
                id = id.lowercase()
            )
            Log.d("RETROFIT", response.toString())
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            ResponseWrapper.NetworkError
        }

}