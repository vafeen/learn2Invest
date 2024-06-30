package ru.surf.learn2invest.noui.database_components.dao.implementation

import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject

/**
 * Интерфейс дополнений для основного [DataAccessObject][ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject]
 * Дополнение: вставка сущностей в базу данных согласно лимиту
 */
interface InsertByLimitImplementation<T> : DataAccessObject<T>,
    FlowGetAllImplementation<T> {

    /**
     *  @param limit [максимальное количество записей в БД. При потенциальном количестве записей больше этого значения, часть записей будет удалена]
     *  @param entities [Список объектов, которые нужно положить в бд]
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