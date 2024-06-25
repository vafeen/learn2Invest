package ru.surf.learn2invest.ui.components.screens.fragments.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction

class HistoryViewModel: ViewModel() {
    val data: Flow<List<Transaction>> = DatabaseRepository.getAllAsFlowTransaction().map { it.reversed() }
}