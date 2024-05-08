package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.TransactionCoinSpecific

interface TransactionCoinSpecificDao : DataAccessObject<TransactionCoinSpecific> {


    @Query("select * from transactioncoinspecific")
    override suspend fun getAll(): List<TransactionCoinSpecific>
}