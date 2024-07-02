package ru.surf.learn2invest.noui.network_components

import android.util.Log
import retrofit2.Retrofit
import ru.surf.learn2invest.noui.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.noui.network_components.responses.CoinPriceResponse
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.noui.network_components.responses.ResponseWrapper
import ru.surf.learn2invest.utils.RetrofitLinks
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для получения данных с API
 */

@Singleton
class NetworkRepository @Inject constructor(retrofit: Retrofit) {
    private val coinAPIService = retrofit.create(
        CoinAPIService::class.java
    )

    /**
     * Выводит информацию о всех коинах на рынке
     **/
    suspend fun getMarketReview(): ResponseWrapper<List<CoinReviewResponse>> =
        try {
            val response = coinAPIService.getMarketReview()
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            ResponseWrapper.NetworkError
        }


    /**
     * выводит историю цен коина за предыдущие 6 дней, не считая сегодня
     * id - идентификатор коина в API
     * например bitcoin
     **/
    suspend fun getCoinHistory(id: String): ResponseWrapper<List<CoinPriceResponse>> =
        try {
            val response = coinAPIService.getCoinHistory(
                id = id.lowercase(),
                interval = RetrofitLinks.INTERVAL,
                start = System.currentTimeMillis() - RetrofitLinks.WEEK,
                end = System.currentTimeMillis()
            )
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            ResponseWrapper.NetworkError
        }


    /**
     * Возвращает подробную информацию о конкретном коине
     * id - идентификатор коина в API
     * например bitcoin
     **/
    suspend fun getCoinReview(id: String): ResponseWrapper<AugmentedCoinReviewResponse> =
        try {
            val response = coinAPIService.getCoinReview(
                id = id.lowercase()
            )
            ResponseWrapper.Success(response.data)
        } catch (e: Exception) {
            ResponseWrapper.NetworkError
        }

}