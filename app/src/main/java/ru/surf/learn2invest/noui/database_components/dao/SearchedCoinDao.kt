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


    /**
     *  @param limit максимальное количество записей в БД.
     *  При потенциальном количестве записей больше этого значения, часть записей будет удалена
     */
    suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) {
        getAll().collect { coinsInDB ->
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
}