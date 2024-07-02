package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction


class SubHistoryFragmentViewModel @AssistedInject constructor(
    var databaseRepository: DatabaseRepository,
    @Assisted val symbol: String
) : ViewModel() {


    var data: Flow<List<Transaction>> =
        databaseRepository.getFilteredBySymbolTransaction(symbol).map { it.reversed() }

    @AssistedFactory
    interface Factory {
        fun createSubHistoryAssetViewModel(symbol: String): SubHistoryFragmentViewModel
    }
}