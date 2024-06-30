package ru.surf.learn2invest.ui.components.screens.fragments.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import javax.inject.Inject

@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(var databaseRepository: DatabaseRepository) :
    ViewModel() {
    val data: Flow<List<Transaction>> =
        databaseRepository.getAllAsFlowTransaction().map { it.reversed() }
}