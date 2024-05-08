package ru.surf.learn2invest.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.TransactionCoinSpecific



@Dao
interface TransactionCoinSpecificDao : DataAccessObject<TransactionCoinSpecific> {


    @Query("select * from transactioncoinspecific")
    fun getAll(): Flow<List<TransactionCoinSpecific>>
}