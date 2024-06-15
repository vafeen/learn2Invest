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

    val chartData: Flow<List<Entry>> =
        assetBalanceHistoryDao.getAllAsFlow().map { balanceHistories ->
            balanceHistories.mapIndexed { index, assetBalanceHistory ->
                Entry(index.toFloat(), assetBalanceHistory.assetBalance)
            }
        }

    val assetBalance: Flow<Float> = profileDao.getAllAsFlow().map { profiles ->
        if (profiles.isNotEmpty()) {
            val profile = profiles[App.idOfProfile]
            profile.assetBalance + profile.fiatBalance
        } else {
            0f
        }
    }

    val fiatBalance: Flow<Float> = profileDao.getAllAsFlow().map { profiles ->
        if (profiles.isNotEmpty()) {
            profiles[App.idOfProfile].fiatBalance
        } else {
            0f
        }
    }

    val assetsFlow: Flow<List<AssetInvest>> = assetInvestDao.getAllAsFlow().onEach { assets ->
        loadPriceChanges(assets)
    }

    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage

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
        val initialBalance = App.profile.assetBalance
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