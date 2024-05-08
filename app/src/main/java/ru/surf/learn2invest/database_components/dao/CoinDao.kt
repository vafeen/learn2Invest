package ru.surf.learn2invest.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Coin


@Dao
interface CoinDao : DataAccessObject<Coin> {
    @Query("select * from coin")
    fun getAll(): Flow<List<Coin>>

}

