package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao

class PortfolioViewModelFactory(
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao,
    private val assetInvestDao: AssetInvestDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(assetBalanceHistoryDao, assetInvestDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}