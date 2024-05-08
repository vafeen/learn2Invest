package ru.surf.learn2invest.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.CoinReview


@Dao
interface CoinReviewDao : DataAccessObject<CoinReview> {

    @Query("select * from coinreview")
    fun getAll(): Flow<List<CoinReview>>

}