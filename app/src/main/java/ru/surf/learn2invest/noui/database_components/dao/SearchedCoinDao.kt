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
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from searchedcoin")
    override fun getAllAsFlow(): Flow<List<SearchedCoin>>

    /**
     * Очистка таблицы
     */
    @Query("delete from searchedcoin")
    override suspend fun clearTable()

    /**
     *  @param limit [максимальное количество записей в БД. При потенциальном количестве записей больше этого значения, часть записей будет удалена]
     *  @param entities [Список объектов, которые нужно положить в бд]
     */
    override suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) {
        super.insertByLimit(limit, *entities)
    }
}
