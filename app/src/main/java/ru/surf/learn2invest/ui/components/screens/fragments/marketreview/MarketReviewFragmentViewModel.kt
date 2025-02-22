package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.network_components.NetworkRepository
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.noui.network_components.responses.ResponseWrapper
import ru.surf.learn2invest.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_MARKETCAP
import ru.surf.learn2invest.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_PERCENT
import ru.surf.learn2invest.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_PRICE
import ru.surf.learn2invest.utils.toCoinReviewDto
import javax.inject.Inject

@HiltViewModel
class MarketReviewFragmentViewModel @Inject constructor(
    var databaseRepository: DatabaseRepository, var networkRepository: NetworkRepository
) : ViewModel() {
    private var _data: MutableStateFlow<MutableList<CoinReviewResponse>> = MutableStateFlow(
        mutableListOf()
    )
    val data: StateFlow<List<CoinReviewResponse>> get() = _data
    private var _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private var _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> get() = _isError
    private var _filterState: MutableStateFlow<Map<Int, Boolean>> = MutableStateFlow(
        mapOf(
            Pair(FILTER_BY_MARKETCAP, true),
            Pair(FILTER_BY_PERCENT, false),
            Pair(FILTER_BY_PRICE, false)
        )
    )
    val filterState: StateFlow<Map<Int, Boolean>> get() = _filterState
    private val _filterOrder: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val filterOrder: StateFlow<Boolean> get() = _filterOrder
    private var firstTimePriceFilter = true
    private val _isSearch: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearch: StateFlow<Boolean> get() = _isSearch
    private var _searchedData: MutableStateFlow<MutableList<CoinReviewResponse>> = MutableStateFlow(
        mutableListOf()
    )
    val searchedData: StateFlow<List<CoinReviewResponse>> get() = _searchedData
    var firstUpdateElement = 0
        private set
    var amountUpdateElement = 0
        private set
    var isRealtimeUpdate = false
        private set

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result: ResponseWrapper<List<CoinReviewResponse>> =
                networkRepository.getMarketReview()) {
                is ResponseWrapper.Success -> {
                    _isLoading.value = false
                    _isError.value = false
                    val temp = result.value.toMutableList()
                    temp.removeIf {
                        it.marketCapUsd == 0.0f
                    }
                    temp.sortByDescending { it.marketCapUsd }
                    _data.value = temp
                }

                is ResponseWrapper.NetworkError -> {
                    _isLoading.value = false
                    _isError.value = true
                }
            }
        }
    }

    private fun activateFilterState(element: Int) {
        if (element != FILTER_BY_PRICE) firstTimePriceFilter = true
        _filterState.update {
            it.mapValues { a -> a.key == element }
        }
    }

    fun filterByMarketcap() {
        activateFilterState(FILTER_BY_MARKETCAP)
        _data.update {
            it.sortedByDescending { element -> element.marketCapUsd }.toMutableList()
        }
    }

    fun filterByPercent() {
        activateFilterState(FILTER_BY_PERCENT)
        _data.update {
            it.sortedByDescending { element -> element.changePercent24Hr }.toMutableList()
        }
    }

    fun filterByPrice() {
        activateFilterState(FILTER_BY_PRICE)
        if (!firstTimePriceFilter) {
            _filterOrder.update {
                it.not()
            }
        } else firstTimePriceFilter = false
        _data.update {
            if (filterOrder.value) it.sortedBy { element -> element.priceUsd }
                .toMutableList()
            else it.sortedByDescending { element -> element.priceUsd }.toMutableList()
        }
    }

    fun setSearchState(state: Boolean, searchRequest: String = "") {
        val tempSearch = mutableListOf<String>()
        _isSearch.update {
            state
        }
        if (state) {
            viewModelScope.launch(Dispatchers.IO) {
                if (searchRequest.isBlank().not()) {
                    databaseRepository.insertAllSearchedCoin(
                        SearchedCoin(coinID = searchRequest)
                    )
                }
                databaseRepository.getAllAsFlowSearchedCoin().first()
                    .map { tempSearch.add(it.coinID) }
                _searchedData.update {
                    _data.value.filter { element -> tempSearch.contains(element.name) }.reversed()
                        .toMutableList()
                }
            }
        }
    }


    fun updateData(firstElement: Int, lastElement: Int) {
        val tempUpdate = mutableListOf<CoinReviewResponse>()
        isRealtimeUpdate = true
        val updateDestinationLink = if (_isSearch.value) _searchedData
        else _data
        if (updateDestinationLink.value.isNotEmpty()
            && firstElement != NO_POSITION
            && updateDestinationLink.value.size > lastElement
        ) {
            firstUpdateElement = firstElement
            amountUpdateElement = lastElement - firstElement + 1
            viewModelScope.launch(Dispatchers.IO) {
                for (index in firstElement..lastElement) {
                    when (val result =
                        networkRepository.getCoinReview(updateDestinationLink.value[index].id)) {
                        is ResponseWrapper.Success -> {
                            tempUpdate.add(result.value.toCoinReviewDto())
                        }

                        is ResponseWrapper.NetworkError -> _isError.value = true
                    }
                }
                val tempUpdateId = tempUpdate.map { it.id }
                updateDestinationLink.update {
                    it.map { element ->
                        if (tempUpdateId.contains(element.id)) tempUpdate.find { updateElement ->
                            updateElement.id == element.id
                        }
                        else element
                    } as MutableList<CoinReviewResponse>
                }
            }
        }
        else initData()
    }

    fun clearSearchData() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteAllSearchedCoin()
            _searchedData.update {
                mutableListOf()
            }
        }
    }
}