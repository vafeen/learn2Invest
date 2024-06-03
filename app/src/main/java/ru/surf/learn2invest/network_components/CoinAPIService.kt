package ru.surf.learn2invest.network_components

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.surf.learn2invest.network_components.responses.APIWrapper
import ru.surf.learn2invest.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.network_components.util.Const.API_HISTORY
import ru.surf.learn2invest.network_components.util.Const.API_MARKET_REVIEW
import ru.surf.learn2invest.network_components.responses.CoinPriceResponse
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.network_components.util.Const.API_COIN_REVIEW

interface CoinAPIService {
    @GET(API_MARKET_REVIEW)
    suspend fun getMarketReview(): APIWrapper<List<CoinReviewResponse>>

    @GET(API_HISTORY)
    suspend fun getCoinHistory(
        @Path("id") id: String,
        @Query("interval") interval: String,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): APIWrapper<List<CoinPriceResponse>>

    @GET(API_COIN_REVIEW)
    suspend fun getCoinReview(
        @Path("id") id: String
    ): APIWrapper<AugmentedCoinReviewResponse>
}