package ru.surf.learn2invest.noui.database_components.dao.parent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update


/**
 * Родительский интерфейс всех DAO с базовыми методами
 */
@Dao
interface DataAccessObject<T> {

    /**
     * Вставка && Обновление в базе данных одного или нескольких объектов
     * @param entities [Список объектов, которые нуно положить в бд]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)// insert && update
    suspend fun insertAll(vararg entities: T)

    /**
     * Обновление в базе данных одного или нескольких объектов
     * @param entities [Список объектов, которые нуно положить в бд]
     */
    @Update
    suspend fun update(vararg entities: T)

    /**
     * Удаление из базы данных одного объекта
     */
    @Delete
    suspend fun delete(entity: T)

}