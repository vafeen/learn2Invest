package ru.surf.learn2invest.database_components.dao.parent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface DataAccessObject<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)// insert && update
    suspend fun insertAll(vararg entities: T)

    @Delete
    suspend fun delete(entity: T)

    suspend fun getAll(): List<T>

}