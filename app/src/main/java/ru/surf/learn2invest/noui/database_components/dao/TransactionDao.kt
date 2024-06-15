package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction


@Dao
interface TransactionDao : DataAccessObject<Transaction>,
    FlowGetAllImplementation<Transaction> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */

    @Query("select * from `transaction`")
    override fun getAllAsFlow(): Flow<List<Transaction>>


    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     * поле `symbol` в которых равно `filterSymbol`
     */
    @Query("SELECT * FROM `transaction` WHERE symbol = :filterSymbol")
    fun getFilteredBySymbol(filterSymbol: String): Flow<List<Transaction>>
}
