package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubHistoryViewModelFactory(private val symbol: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubHistoryViewModel(symbol) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}