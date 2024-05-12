package ru.surf.learn2invest.network_components

import android.util.Log
import retrofit2.HttpException
import ru.surf.learn2invest.network_components.responses.CoinHistoryResponse
import ru.surf.learn2invest.network_components.responses.MarketReviewResponse
import ru.surf.learn2invest.network_components.util.CoinRetrofitClient
import ru.surf.learn2invest.network_components.util.Const

/**
 * Пример использования:
 * this.lifecycleScope.launch {
 *   lateinit var result1: ResponseWrapper<MarketReviewResponse>
 *   lateinit var result2: ResponseWrapper<CoinHistoryResponse>
 *   withContext(Dispatchers.IO) {
 *       result1 = coinClient.getMarketReview()
 *       result2 = coinClient.getCoinHistory("monero")
 *   }
 *   when (result1) {
 *       is ResponseWrapper.Success -> Log.d("SUCCESS", (result1 as ResponseWrapper.Success<MarketReviewResponse>).value.data.toString())
 *       is ResponseWrapper.NetworkError -> Log.d("FAIL", "network error")
 *   }
 *   when (result2) {
 *       is ResponseWrapper.Success -> Log.d("SUCCESS", (result2 as ResponseWrapper.Success<CoinHistoryResponse>).value.data.toString())
 *       is ResponseWrapper.NetworkError -> Log.d("FAIL", "network error")
 *   }
* }
**/
class NetworkRepository {
    private val coinAPIService = CoinRetrofitClient.client.create(
        CoinAPIService::class.java
    )

    suspend fun getMarketReview(): ResponseWrapper<MarketReviewResponse> {
        try {
            val response = coinAPIService.getMarketReview()
            Log.d("RETROFIT", response.toString())
            return ResponseWrapper.Success(response)
        }
        catch (e: HttpException) {
            Log.d("RETROFIT", "HTTP Error: ${e.code()}")
            return ResponseWrapper.NetworkError
        }
        catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            return ResponseWrapper.NetworkError
        }
    }
    suspend fun getCoinHistory(id: String): ResponseWrapper<CoinHistoryResponse> {
        try {
            val response = coinAPIService.getCoinHistory(
                id = id,
                interval = Const.INTERVAL,
                start = System.currentTimeMillis() - Const.WEEK,
                end = System.currentTimeMillis()
            )
            Log.d("RETROFIT", response.toString())
            return ResponseWrapper.Success(response)
        }
        catch (e: HttpException) {
            Log.d("RETROFIT", "HTTP Error: ${e.code()}")
            return ResponseWrapper.NetworkError
        }
        catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            return ResponseWrapper.NetworkError
        }
    }
}