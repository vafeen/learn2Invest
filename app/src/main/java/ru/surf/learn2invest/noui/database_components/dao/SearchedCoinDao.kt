package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.InsertByLimitImplementation
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin

@Dao
interface SearchedCoinDao : DataAccessObject<SearchedCoin>,
    FlowGetAllImplementation<SearchedCoin>,
    InsertByLimitImplementation<SearchedCoin> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from searchedcoin")
    override fun getAllAsFlow(): Flow<List<SearchedCoin>>

    @Query("delete from searchedcoin")
    suspend fun deleteAll()

    override suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) {
        super.insertByLimit(limit, *entities)
    }
}
