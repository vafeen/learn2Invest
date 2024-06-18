package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date

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

    init {
        viewModelScope.launch {
            checkAndUpdateBalanceHistory()
        }
    }

    private suspend fun checkAndUpdateBalanceHistory() {
        val balanceHistories = DatabaseRepository.getAllAssetBalanceHistory().first()
        val today = Date()
        val todayStart = today.time - (today.time % (24 * 60 * 60 * 1000))
        val todayDate = Date(todayStart)

        val todayBalanceHistory = balanceHistories.find {
            val historyDateStart = it.date.time - (it.date.time % (24 * 60 * 60 * 1000))
            historyDateStart == todayStart
        }

        val assetBalance = DatabaseRepository.getAllAsFlowProfile().firstOrNull()?.firstOrNull()?.assetBalance ?: 0f

        if (todayBalanceHistory != null) {
            // Update today's balance
            DatabaseRepository.updateAssetBalanceHistory(
                todayBalanceHistory.copy(assetBalance = assetBalance)
            )
        } else {
            // Insert new balance history for today
            DatabaseRepository.insertAllAssetBalanceHistory(
                AssetBalanceHistory(assetBalance = assetBalance, date = todayDate)
            )
        }
    }

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