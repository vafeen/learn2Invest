package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory




@Dao
interface AssetBalanceHistoryDao : DataAccessObject<AssetBalanceHistory>,
    FlowGetAllImplementation<AssetBalanceHistory> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from assetbalancehistory")
    override fun getAllAsFlow(): Flow<List<AssetBalanceHistory>>

}
