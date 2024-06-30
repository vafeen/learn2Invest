package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import javax.inject.Inject

@HiltViewModel
class SubHistoryFragmentViewModel @Inject constructor(@Inject var databaseRepository: DatabaseRepository) :
    ViewModel() {

    lateinit var symbol: String
    lateinit var data: Flow<List<Transaction>>
}