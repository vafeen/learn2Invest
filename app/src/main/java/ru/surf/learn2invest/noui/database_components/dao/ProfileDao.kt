package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.Profile


@Dao
interface ProfileDao : DataAccessObject<Profile> {


    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных
     */
    @Query("select * from profile")
    suspend fun getProfile(): List<Profile>

    @Query("select * from profile")
    suspend fun getProfileAsFlow(): Flow<List<Profile>>

}

