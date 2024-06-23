package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction

class SubHistoryViewModel(val symbol: String): ViewModel() {
    val data: Flow<List<Transaction>> = DatabaseRepository.getFilteredBySymbolTransaction(symbol)
}