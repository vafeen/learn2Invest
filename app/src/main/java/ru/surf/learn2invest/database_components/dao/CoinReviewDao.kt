package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.CoinReview

interface CoinReviewDao : DataAccessObject<CoinReview> {

    @Query("select * from coinreview")
    override suspend fun getAll(): List<CoinReview>

}