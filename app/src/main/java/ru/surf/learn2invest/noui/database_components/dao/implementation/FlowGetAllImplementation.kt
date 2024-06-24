package ru.surf.learn2invest.noui.database_components.dao.implementation

import kotlinx.coroutines.flow.Flow

interface FlowGetAllImplementation<T> {
    fun getAllAsFlow(): Flow<List<T>>
}
