package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.implementation.InsertByLimitImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import java.util.Date


@Dao
interface AssetBalanceHistoryDao : DataAccessObject<AssetBalanceHistory>,
    FlowGetAllImplementation<AssetBalanceHistory>,
    InsertByLimitImplementation<AssetBalanceHistory> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from assetbalancehistory")
    override fun getAllAsFlow(): Flow<List<AssetBalanceHistory>>

    @Query("SELECT * FROM AssetBalanceHistory WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Date): AssetBalanceHistory?

    override suspend fun insertByLimit(limit: Int, vararg entities: AssetBalanceHistory) {
        super.insertByLimit(limit = limit, entities = entities)
    }
}
