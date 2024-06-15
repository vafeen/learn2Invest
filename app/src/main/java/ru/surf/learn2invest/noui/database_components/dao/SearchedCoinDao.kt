package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.dao.parent.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin

@Dao
interface SearchedCoinDao : DataAccessObject<SearchedCoin>,
    FlowGetAllImplementation<SearchedCoin> {

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from searchedcoin")
    override fun getAllAsFlow(): Flow<List<SearchedCoin>>

    @Query("delete from searchedcoin")
    suspend fun deleteAll()

    /**
     *  @param limit максимальное количество записей в БД.
     *  При потенциальном количестве записей больше этого значения, часть записей будет удалена
     */
    suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) {
        val coinsInDB = getAllAsFlow().first()
        val resultSize = coinsInDB.size + entities.size
        if (resultSize > limit) {
            val countToDel = coinsInDB.size + entities.size - limit
            for (index in coinsInDB.size - countToDel..<coinsInDB.size) {
                delete(coinsInDB[index])
            }
        }
        insertAll(*entities)
    }
}
