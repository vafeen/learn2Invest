package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioViewModel : ViewModel() {

    val chartData: Flow<List<Entry>> =
        DatabaseRepository.getAllAssetBalanceHistory().map { balanceHistories ->
            balanceHistories.mapIndexed { index, assetBalanceHistory ->
                Entry(index.toFloat(), assetBalanceHistory.assetBalance)
            }
        }

    val assetBalance: Flow<Float> = DatabaseRepository.getAllAsFlowProfile().map { profiles ->
        if (profiles.isNotEmpty()) {
            profiles[App.idOfProfile].assetBalance
        } else {
            0f
        }
    }

    val fiatBalance: Flow<Float> = DatabaseRepository.getAllAsFlowProfile().map { profiles ->
        if (profiles.isNotEmpty()) {
            profiles[App.idOfProfile].fiatBalance
        } else {
            0f
        }
    }

    val assetsFlow: Flow<List<AssetInvest>> =
        DatabaseRepository.getAllAsFlowAssetInvest().onEach { assets ->
            loadPriceChanges(assets)
        }

    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage

    private suspend fun loadPriceChanges(assets: List<AssetInvest>) {
        val priceChanges = mutableMapOf<String, Float>()
        var totalCurrentValue = 0f
        var initialInvestment = 0f
        var currentPrice: Float
        for (asset in assets) {
            val response = NetworkRepository.getCoinReview(asset.assetID)
            if (response is ResponseWrapper.Success) {
                currentPrice = response.value.data.priceUsd
                priceChanges[asset.symbol] = currentPrice
                totalCurrentValue += currentPrice * asset.amount
                initialInvestment += asset.coinPrice * asset.amount
            }
        }
        _priceChanges.value = priceChanges
        calculatePortfolioChangePercentage(totalCurrentValue, initialInvestment)
    }

    private fun calculatePortfolioChangePercentage(
        totalCurrentValue: Float,
        initialInvestment: Float
    ) {
        if (initialInvestment != 0f) {
            val changePercentage =
                ((totalCurrentValue - initialInvestment) / initialInvestment) * 100
            val roundedChangePercentage =
                BigDecimal(changePercentage.toString()).setScale(2, RoundingMode.HALF_UP)
                    .toFloat()
            _portfolioChangePercentage.value = roundedChangePercentage
        } else {
            _portfolioChangePercentage.value
        }
    }
}