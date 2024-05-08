package ru.surf.learn2invest.database_components.dao

import androidx.room.Query
import ru.surf.learn2invest.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.database_components.entity.Profile

interface ProfileDao : DataAccessObject<Profile> {

    @Query("select * from profile")
    override suspend fun getAll(): List<Profile>

}
