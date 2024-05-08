package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Transaction

interface TransactionDao : DataAccessObject<Transaction> {

    @Query("select * from `transaction`")
    override suspend fun getAll(): List<Transaction>

}