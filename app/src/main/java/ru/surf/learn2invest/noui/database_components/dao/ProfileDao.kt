package ru.surf.learn2invest.noui.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.noui.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.noui.database_components.entity.Profile


@Dao
interface ProfileDao : DataAccessObject<Profile>,
    FlowGetAllImplementation<Profile> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from profile")
    override fun getAllAsFlow(): Flow<List<Profile>>
}

