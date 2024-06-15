package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.logs.Loher
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioViewModel : ViewModel() {
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao = App.mainDB.assetBalanceHistoryDao()
    private val assetInvestDao: AssetInvestDao = App.mainDB.assetInvestDao()
    private val networkRepository = NetworkRepository()

    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    private val _assetBalance = MutableLiveData<Float>()
    val assetBalance: LiveData<Float> get() = _assetBalance

    private val _fiatBalance = MutableLiveData<Float>()
    val fiatBalance: LiveData<Float> get() = _fiatBalance

    val assets: MutableList<AssetInvest> = mutableListOf()

    private val _priceChanges = MutableLiveData<Map<String, Float>>()
    val priceChanges: LiveData<Map<String, Float>> get() = _priceChanges


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

    fun loadAssetsData() {
        viewModelScope.launch(Dispatchers.IO) {
            assetInvestDao.getAllAsFlow().collect {
                withContext(Dispatchers.Main) {
                    assets.clear()
                    assets.addAll(it)
                    loadPriceChanges()
                }
            }
        }
    }

    fun loadBalanceData() {
        viewModelScope.launch(Dispatchers.IO) {
            _assetBalance.postValue(App.profile.assetBalance)
            _fiatBalance.postValue(App.profile.fiatBalance)
        }
    }

    private fun loadPriceChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            val priceChanges = mutableMapOf<String, Float>()
            for (asset in assets) {
                val response = networkRepository.getCoinReview(asset.assetID)
                if (response is ResponseWrapper.Success) {
                    val currentPrice = response.value.data.priceUsd
                    Loher.d("current price $currentPrice")
                    val priceChange = ((currentPrice - asset.coinPrice) / asset.coinPrice) * 100
                    val roundedPriceChange = BigDecimal(priceChange.toString()).setScale(2, RoundingMode.HALF_UP).toFloat()
                    priceChanges[asset.symbol] = roundedPriceChange
                }
            }
            withContext(Dispatchers.Main) {
                _priceChanges.value = priceChanges
            }
        }
    }
}