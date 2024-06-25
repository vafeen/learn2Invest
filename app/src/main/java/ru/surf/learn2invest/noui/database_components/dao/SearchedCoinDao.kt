package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.implementation.ClearTableImplementation
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.implementation.InsertByLimitImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin

@Dao
interface SearchedCoinDao : DataAccessObject<SearchedCoin>,
    FlowGetAllImplementation<SearchedCoin>,
    InsertByLimitImplementation<SearchedCoin>,
    ClearTableImplementation {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from searchedcoin")
    override fun getAllAsFlow(): Flow<List<SearchedCoin>>

    @Query("delete from searchedcoin")
    override suspend fun clearTable()

    override suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) {
        super.insertByLimit(limit, *entities)
    }
}
