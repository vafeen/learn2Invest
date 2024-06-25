package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.implementation.InsertByLimitImplementation
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import java.util.Date


@Dao
interface AssetBalanceHistoryDao : DataAccessObject<AssetBalanceHistory>,
    FlowGetAllImplementation<AssetBalanceHistory>,
    InsertByLimitImplementation<AssetBalanceHistory> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from assetbalancehistory")
    override fun getAllAsFlow(): Flow<List<AssetBalanceHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(vararg entities: AssetBalanceHistory)

    @Update
    override suspend fun update(vararg entities: AssetBalanceHistory)

    @Query("DELETE FROM AssetBalanceHistory WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM AssetBalanceHistory WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Date): AssetBalanceHistory?

    override suspend fun insertByLimit(limit: Int, vararg entities: AssetBalanceHistory) {
        super.insertByLimit(limit = limit, entities = entities)
    }
}
