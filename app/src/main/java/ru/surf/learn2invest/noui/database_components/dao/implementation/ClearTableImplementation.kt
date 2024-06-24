package ru.surf.learn2invest.noui.database_components.dao.implementation

import androidx.room.Dao

@Dao
interface ClearTableImplementation {

    suspend fun clearTable()

}