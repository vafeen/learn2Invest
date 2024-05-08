package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.AssetInvest


interface AssetInvestDao : DataAccessObject<AssetInvest> {

    @Query("select * from assetinvest")
    override suspend fun getAll(): List<AssetInvest>


}