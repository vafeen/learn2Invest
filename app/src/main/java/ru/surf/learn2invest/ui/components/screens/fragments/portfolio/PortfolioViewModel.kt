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
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest

class PortfolioViewModel(
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao,
    private val assetInvestDao: AssetInvestDao
) : ViewModel() {
    private val _chartData = MutableLiveData<List<Entry>>()
    val chartData: LiveData<List<Entry>> get() = _chartData

    private val _assetBalance = MutableLiveData<Float>()
    val assetBalance: LiveData<Float> get() = _assetBalance

    private val _fiatBalance = MutableLiveData<Float>()
    val fiatBalance: LiveData<Float> get() = _fiatBalance

    val assets: MutableList<AssetInvest> = mutableListOf()

    fun loadChartData() {
        viewModelScope.launch(Dispatchers.IO) {
            _assetBalance.postValue(App.profile.assetBalance)
            _fiatBalance.postValue(App.profile.fiatBalance)
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
                assets.addAll(it)
            }
        }
    }

    fun loadBalanceData() {
        viewModelScope.launch(Dispatchers.IO) {
            _assetBalance.postValue(App.profile.assetBalance)
            _fiatBalance.postValue(App.profile.fiatBalance)
        }
    }
}