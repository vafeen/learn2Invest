package ru.surf.learn2invest.network_components

import android.util.Log
import retrofit2.HttpException
import ru.surf.learn2invest.network_components.responses.APIWrapper
import ru.surf.learn2invest.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.network_components.responses.CoinPriceDto
import ru.surf.learn2invest.network_components.responses.CoinReviewDto
import ru.surf.learn2invest.network_components.util.CoinRetrofitClient
import ru.surf.learn2invest.network_components.util.Const
import ru.surf.learn2invest.noui.logs.Loher

/**
 * Пример использования (Вариант для запуска из Activity):
 * ```
 * val coinClient = NetworkRepository()
 * override fun onResume() {
 *   super.onResume()
 *   this.lifecycleScope.launch {
 *      lateinit var result1: ResponseWrapper<MarketReviewResponse>
 *      lateinit var result2: ResponseWrapper<CoinHistoryResponse>
 *      withContext(Dispatchers.IO) {
 *          result1 = coinClient.getMarketReview()
 *          result2 = coinClient.getCoinHistory("monero")
 *      }
 *      when (result1) {
 *           is ResponseWrapper.Success -> Log.d("SUCCESS", (result1 as ResponseWrapper.Success<MarketReviewResponse>).value.data.toString())
 *           is ResponseWrapper.NetworkError -> Log.d("FAIL", "network error")
 *      }
 *      when (result2) {
 *          is ResponseWrapper.Success -> Log.d("SUCCESS", (result2 as ResponseWrapper.Success<CoinHistoryResponse>).value.data.toString())
 *          is ResponseWrapper.NetworkError -> Log.d("FAIL", "network error")
 *      }
 *   }
 * }
 * ```
 **/
object NetworkRepository {
    private val coinAPIService = CoinRetrofitClient.client.create(
        CoinAPIService::class.java
    )

    suspend fun getMarketReview(): ResponseWrapper<APIWrapper<List<CoinReviewDto>>> {
        try {
            val response = coinAPIService.getMarketReview()
            Log.d("RETROFIT", response.toString())
            return ResponseWrapper.Success(response)
        } catch (e: HttpException) {
            Log.d("RETROFIT", "HTTP Error: ${e.code()}")
            return ResponseWrapper.NetworkError
        } catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            return ResponseWrapper.NetworkError
        }
    }

    /**
     * выводит историю цен коина за предыдущие 6 дней, не считая сегодня
     * id - название коина
     * например bitcoin
     **/
    suspend fun getCoinHistory(id: String): ResponseWrapper<APIWrapper<List<CoinPriceDto>>> {
        try {
            val response = coinAPIService.getCoinHistory(
                id = id.lowercase(),
                interval = Const.INTERVAL,
                start = System.currentTimeMillis() - Const.WEEK,
                end = System.currentTimeMillis()
            )
            Log.d("RETROFIT", response.toString())
            return ResponseWrapper.Success(response)
        } catch (e: HttpException) {
            Log.d("RETROFIT", "HTTP Error: ${e.code()}")
            return ResponseWrapper.NetworkError
        } catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            return ResponseWrapper.NetworkError
        }
    }

    suspend fun getCoinReview(id: String): ResponseWrapper<APIWrapper<AugmentedCoinReviewResponse>> {
        try {
            val response = coinAPIService.getCoinReview(
                id = id.lowercase()
            )
            Log.d("RETROFIT", response.toString())
            return ResponseWrapper.Success(response)
        } catch (e: HttpException) {
            Log.d("RETROFIT", "HTTP Error: ${e.code()}")
            Loher.d(e.toString())
            return ResponseWrapper.NetworkError
        } catch (e: Exception) {
            Log.d("RETROFIT", "Error: ${e.message}")
            Loher.d(e.toString())
            return ResponseWrapper.NetworkError
        }
    }
}