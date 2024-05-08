package ru.surf.learn2invest.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Transaction


@Dao
interface TransactionDao : DataAccessObject<Transaction> {

    @Query("select * from `transaction`")
    fun getAll(): Flow<List<Transaction>>

}
