package ru.surf.learn2invest.noui.database_components.dao.implementation

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс дополнений для основного [DataAccessObject][ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject]
 * Дополнение: получение всех сущностей в виде Flow
 */
interface FlowGetAllImplementation<T> {
    /**
     * Получение всех сущностей в виде Flow
     */
    fun getAllAsFlow(): Flow<List<T>>
}
