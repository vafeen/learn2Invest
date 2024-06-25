package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction

class SubHistoryViewModel(val symbol: String): ViewModel() {
    val data: Flow<List<Transaction>> = DatabaseRepository.getFilteredBySymbolTransaction(symbol).map { it.reversed() }
}