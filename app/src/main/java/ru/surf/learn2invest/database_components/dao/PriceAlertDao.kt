package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.PriceAlert

interface PriceAlertDao : DataAccessObject<PriceAlert> {


    @Query("select * from pricealert")
    override suspend fun getAll(): List<PriceAlert>

}