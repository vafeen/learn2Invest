package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.logs.Loher
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioViewModel : ViewModel() {
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao = App.mainDB.assetBalanceHistoryDao()
    private val assetInvestDao: AssetInvestDao = App.mainDB.assetInvestDao()
    private val profileDao: ProfileDao = App.mainDB.profileDao()
    private val networkRepository = NetworkRepository()

    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    val assetBalance: Flow<Float> = profileDao.getAllAsFlow().map { profiles ->
        val profile = profiles[App.idOfProfile]
        profile.assetBalance + profile.fiatBalance
    }

    val fiatBalance: Flow<Float> = profileDao.getAllAsFlow().map { profiles ->
        profiles[App.idOfProfile].fiatBalance
    }

    val assetsFlow: Flow<List<AssetInvest>> = assetInvestDao.getAllAsFlow().onEach { assets ->
        loadPriceChanges(assets)
    }

    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage

    fun loadChartData() {
        viewModelScope.launch(Dispatchers.IO) {
            assetBalanceHistoryDao.getAllAsFlow().collect { balanceHistories ->
                val data = balanceHistories.mapIndexed { index, assetBalanceHistory ->
                    Entry(index.toFloat(), assetBalanceHistory.assetBalance)
                }
                withContext(Dispatchers.Main) {
                    _chartData.value = data
                }
            }
        }
    }

    private suspend fun loadPriceChanges(assets: List<AssetInvest>) {
        val priceChanges = mutableMapOf<String, Float>()
        var totalCurrentValue = 0f
        for (asset in assets) {
            val response = networkRepository.getCoinReview(asset.assetID)
            if (response is ResponseWrapper.Success) {
                val currentPrice = response.value.data.priceUsd
                Loher.d("current price $currentPrice")
                val priceChange = ((currentPrice - asset.coinPrice) / asset.coinPrice) * 100
                val roundedPriceChange =
                    BigDecimal(priceChange.toString()).setScale(2, RoundingMode.HALF_UP).toFloat()
                priceChanges[asset.symbol] = roundedPriceChange
                totalCurrentValue += currentPrice * asset.amount
            }
        }
        totalCurrentValue += App.profile.fiatBalance
        _priceChanges.value = priceChanges
        calculatePortfolioChangePercentage(totalCurrentValue)
    }

    private fun calculatePortfolioChangePercentage(totalCurrentValue: Float) {
        val initialBalance = App.profile.assetBalance + App.profile.fiatBalance
        if (initialBalance != 0f) {
            val changePercentage = ((totalCurrentValue - initialBalance) / initialBalance) * 100
            val roundedChangePercentage =
                BigDecimal(changePercentage.toString()).setScale(2, RoundingMode.HALF_UP).toFloat()
            _portfolioChangePercentage.value = roundedChangePercentage
        } else {
            _portfolioChangePercentage.value = 0f
        }
    }
}