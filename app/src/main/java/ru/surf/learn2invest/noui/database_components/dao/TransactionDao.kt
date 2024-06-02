package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.entity.Transaction


@Dao
interface TransactionDao : DataAccessObject<Transaction>,
    FlowGetAllImplementation<Transaction> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */

    @Query("select * from `transaction`")
    override fun getAllAsFlow(): Flow<List<Transaction>>
}
