package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction


@Dao
interface TransactionDao : DataAccessObject<Transaction>,
    FlowGetAllImplementation<Transaction> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from `transaction`")
    override fun getAllAsFlow(): Flow<List<Transaction>>

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных в виде Flow
     * @param filterSymbol [symbol, который должен быть у искомых сущностей]
     */
    @Query("SELECT * FROM `transaction` WHERE symbol = :filterSymbol")
    fun getFilteredBySymbol(filterSymbol: String): Flow<List<Transaction>>
}
