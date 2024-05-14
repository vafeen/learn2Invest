package ru.surf.learn2invest.noui.database_components.dao.parent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy


/**
 * Родительский интерфейс всех DAO с базовыми методами
 */
@Dao
interface DataAccessObject<T> {

    /**
     * Вставка в базу данных одного или нескольких объектов
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)// insert && update
    suspend fun insertAll(vararg entities: T)

    /**
     * Удаление из базы данных одного объекта
     */
    @Delete
    suspend fun delete(entity: T)

}