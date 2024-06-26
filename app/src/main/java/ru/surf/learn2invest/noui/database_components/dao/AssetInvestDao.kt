package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest


@Dao
interface AssetInvestDao : DataAccessObject<AssetInvest>,
    FlowGetAllImplementation<AssetInvest> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from assetinvest")
    override fun getAllAsFlow(): Flow<List<AssetInvest>>

    /**
     * Получение сущности по symbol
     * @param symbol [symbol, который должен быть у искомой сущности]
     */
    @Query("select * from assetinvest where symbol=:symbol")
    suspend fun getBySymbol(symbol: String): AssetInvest?
}

