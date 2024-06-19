package ru.surf.learn2invest.noui.database_components.dao.parent

import kotlinx.coroutines.flow.first


interface InsertByLimitImplementation<T> : DataAccessObject<T>,
    FlowGetAllImplementation<T> {

    /**
     *  @param limit максимальное количество записей в БД.
     *  При потенциальном количестве записей больше этого значения, часть записей будет удалена
     */
    suspend fun insertByLimit(limit: Int, vararg entities: T) {
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