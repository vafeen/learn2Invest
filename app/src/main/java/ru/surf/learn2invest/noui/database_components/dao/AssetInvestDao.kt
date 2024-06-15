package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest


@Dao
interface AssetInvestDao : DataAccessObject<AssetInvest>,
    FlowGetAllImplementation<AssetInvest> {


    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */

    @Query("select * from assetinvest")
    override fun getAllAsFlow(): Flow<List<AssetInvest>>


    @Query("select * from assetinvest where symbol=:symbol")
    suspend fun getBySymbol(symbol: String): AssetInvest?
}

