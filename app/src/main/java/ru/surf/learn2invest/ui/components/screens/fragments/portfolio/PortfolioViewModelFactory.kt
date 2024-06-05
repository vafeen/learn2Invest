package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao

class PortfolioViewModelFactory(
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(assetBalanceHistoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}