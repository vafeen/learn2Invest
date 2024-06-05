package ru.surf.learn2invest.ui.components.screens.fragments.asset_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.surf.learn2invest.network_components.CoinAPIService

class AssetOverviewViewModelFactory(
    private val coinAPIService: CoinAPIService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssetOverviewViewModel::class.java)) {
            return AssetOverviewViewModel(coinAPIService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}