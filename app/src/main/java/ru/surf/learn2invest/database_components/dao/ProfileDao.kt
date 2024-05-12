package ru.surf.learn2invest.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Profile


@Dao
interface ProfileDao : DataAccessObject<Profile> {


    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from profile")
    fun getProfile(): List<Profile>

}
