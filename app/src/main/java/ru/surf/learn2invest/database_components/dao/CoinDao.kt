package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Coin

interface CoinDao : DataAccessObject<Coin> {
    @Query("select * from coin")
    override suspend fun getAll(): List<Coin>

}