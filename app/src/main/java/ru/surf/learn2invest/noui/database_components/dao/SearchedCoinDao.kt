package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin

interface SearchedCoinDao : DataAccessObject<SearchedCoin> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from searchedcoin")
    fun getAll(): Flow<List<SearchedCoin>>

}