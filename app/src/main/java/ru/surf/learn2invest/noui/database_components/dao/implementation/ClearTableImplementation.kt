package ru.surf.learn2invest.noui.database_components.dao.implementation

import androidx.room.Dao

/**
 * Интерфейс дополнений для основного [DataAccessObject][ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject]
 * Дополнение: очистка таблицы
 */
@Dao
interface ClearTableImplementation {

    /**
     * Очистка таблицы
     */
    suspend fun clearTable()
}