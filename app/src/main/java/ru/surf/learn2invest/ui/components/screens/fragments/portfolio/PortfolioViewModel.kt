package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.surf.learn2invest.app.App

import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

class PortfolioViewModel : ViewModel() {

    private val _chartData = MutableStateFlow<List<Entry>>(emptyList())
    val chartData: StateFlow<List<Entry>> = _chartData
    val assetBalance: Flow<Float> = DatabaseRepository.getAllAsFlowProfile()
        .map { profiles ->
            if (profiles.isNotEmpty()) {
                DatabaseRepository.profile.assetBalance
            } else {
                0f
            }
        }
        .flowOn(Dispatchers.IO)

    val fiatBalance: Flow<Float> = DatabaseRepository.getAllAsFlowProfile()
        .map { profiles ->
            if (profiles.isNotEmpty()) {
                DatabaseRepository.profile.fiatBalance
            } else {
                0f
            }
        }
        .flowOn(Dispatchers.IO)

    val totalBalance: Flow<Float> =
        combine(assetBalance, fiatBalance) { assetBalance, fiatBalance ->
            assetBalance + fiatBalance
        }
    val assetsFlow: Flow<List<AssetInvest>> = DatabaseRepository.getAllAsFlowAssetInvest()
        .flowOn(Dispatchers.IO)
        .onEach { assets ->
            loadPriceChanges(assets)
        }

    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage

    suspend fun refreshData() {
        checkAndUpdateBalanceHistory()
        loadPriceChanges(DatabaseRepository.getAllAsFlowAssetInvest().first())
        refreshChartData()
    }

    private suspend fun refreshChartData() {
        _chartData.value = DatabaseRepository.getAllAssetBalanceHistory().first()
            .mapIndexed { index, assetBalanceHistory ->
                Entry(index.toFloat(), assetBalanceHistory.assetBalance)
            }
    }

    private suspend fun checkAndUpdateBalanceHistory() {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val todayDate = today.time

        val todayBalanceHistory = DatabaseRepository.getAllAssetBalanceHistory().first().find {
            val historyDate = Calendar.getInstance()
            historyDate.time = it.date
            historyDate.set(Calendar.HOUR_OF_DAY, 0)
            historyDate.set(Calendar.MINUTE, 0)
            historyDate.set(Calendar.SECOND, 0)
            historyDate.set(Calendar.MILLISECOND, 0)

            historyDate.time == todayDate
        }

        val totalBalance = assetBalance.first() + fiatBalance.first()

        if (todayBalanceHistory != null) {
            DatabaseRepository.updateAssetBalanceHistory(
                todayBalanceHistory.copy(assetBalance = totalBalance)
            )
        } else {
            DatabaseRepository.insertByLimitAssetBalanceHistory(
                7, AssetBalanceHistory(assetBalance = totalBalance, date = todayDate)
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

                val investAsset = DatabaseRepository.getBySymbolAssetInvest(asset.symbol)
                if (investAsset != null) {
                    initialInvestment += investAsset.coinPrice * asset.amount
                }
            }
        }

        DatabaseRepository.profile.also {

            DatabaseRepository.updateProfile(
                it.copy(
                    assetBalance = totalCurrentValue
                )
            )
        }

        _priceChanges.value = priceChanges
        calculatePortfolioChangePercentage(totalCurrentValue, initialInvestment)
    }

    private suspend fun calculatePortfolioChangePercentage(
        totalCurrentValue: Float,
        initialInvestment: Float
    ) {
        if (initialInvestment != 0f) {
            val portfolioChangePercentage =
                ((totalCurrentValue + assetBalance.first()) / (initialInvestment + assetBalance.first()) - 1) * 100
            val roundedPercentage = BigDecimal(portfolioChangePercentage.toDouble())
                .setScale(2, RoundingMode.HALF_UP)
                .toFloat()
            _portfolioChangePercentage.value = roundedPercentage
        } else {
            _portfolioChangePercentage.value = 0f
        }
    }
}