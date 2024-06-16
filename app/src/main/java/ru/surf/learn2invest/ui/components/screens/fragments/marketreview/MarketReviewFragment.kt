package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.network_components.responses.APIWrapper
import ru.surf.learn2invest.network_components.responses.CoinReviewDto
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.network_components.responses.toCoinReviewDto
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity


class MarketReviewFragment : Fragment() {
    private val binding by lazy { FragmentMarketReviewBinding.inflate(layoutInflater) }
    private var recyclerData = mutableListOf<CoinReviewDto>()
    private var data = mutableListOf<CoinReviewDto>()
    private val adapter = MarketReviewAdapter(recyclerData) { coin ->
        startAssetReviewIntent(coin)
    }
    private var filterByPriceFLag = false
    private var filterByPriceIsFirstActive = true
    private lateinit var realTimeUpdateJob: Job
    private var realTimeUpdateSemaphore = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            filterByMarketcap.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.main_background))
            filterByChangePercent24Hr.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.view_background))
            filterByPrice.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.view_background))

            filterByMarketcap.setOnClickListener {
                realTimeUpdateSemaphore = true
                filterByPriceIsFirstActive = true
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.marketCapUsd }
                adapter.notifyDataSetChanged()
                realTimeUpdateSemaphore = false
            }

            filterByChangePercent24Hr.setOnClickListener {
                realTimeUpdateSemaphore = true
                filterByPriceIsFirstActive = true
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.changePercent24Hr }
                adapter.notifyDataSetChanged()
                realTimeUpdateSemaphore = false
            }

            filterByPrice.setOnClickListener {
                realTimeUpdateSemaphore = true
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                if (filterByPriceIsFirstActive.not())
                    filterByPriceFLag = filterByPriceFLag.not()
                if (filterByPriceFLag) {
                    recyclerData.sortByDescending { it.priceUsd }
                    Loher.d(recyclerData.find { it.symbol == "CJ" }.toString())
                    filterByPrice.setIconResource(R.drawable.arrow_bottom_red)
                    filterByPrice.setIconTintResource(R.color.recession)
                } else {
                    recyclerData.sortBy { it.priceUsd }
                    filterByPrice.setIconResource(R.drawable.arrow_top_green)
                    filterByPrice.setIconTintResource(R.color.label)
                }
                adapter.notifyDataSetChanged()

                filterByPriceIsFirstActive = false
                realTimeUpdateSemaphore = false
            }
            textInputLayout.setEndIconOnClickListener {
                realTimeUpdateSemaphore = true
                textView2.isVisible = false
                clearTv.isVisible = false
                filterByPrice.isVisible = true
                filterByMarketcap.isVisible = true
                filterByChangePercent24Hr.isVisible = true
                searchEditText.text.clear()
                recyclerData.clear()
                recyclerData.addAll(data.sortedByDescending { it.marketCapUsd })
                adapter.notifyDataSetChanged()
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                realTimeUpdateSemaphore = false
            }

            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                realTimeUpdateSemaphore = true
                textView2.isVisible = true
                clearTv.isVisible = true
                filterByPrice.visibility = INVISIBLE
                filterByMarketcap.visibility = INVISIBLE
                filterByChangePercent24Hr.visibility = INVISIBLE
                searchEditText.hint = ""
            }

            clearTv.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    realTimeUpdateSemaphore = true
                    DatabaseRepository.deleteAllSearchedCoin()
                    withContext(Dispatchers.Main) {
                        recyclerData.clear()
                        adapter.notifyDataSetChanged()
                    }
                    realTimeUpdateSemaphore = false
                }
            }

            searchEditText.setOnItemClickListener { parent, view, position, id ->
                val searchedList = mutableListOf<String>()
                lifecycleScope.launch(Dispatchers.IO) {
                    realTimeUpdateSemaphore = true
                    DatabaseRepository.insertAllSearchedCoin(
                        SearchedCoin(coinID = searchEditText.text.toString())
                    )
                    DatabaseRepository.getAllAsFlowSearchedCoin()
                        .first().map { searchedList.add(it.coinID) }
                    withContext(Dispatchers.Main) {
                        recyclerData.clear()
                        recyclerData.addAll(data.filter { searchedList.contains(it.name) })
                        recyclerData.reverse()
                        adapter.notifyDataSetChanged()
                        Loher.d(searchedList.toString())
                        realTimeUpdateSemaphore = false
                    }
                }
            }
        }
        setLoading()

        this.lifecycleScope.launch(Dispatchers.IO) {
            val result: ResponseWrapper<APIWrapper<List<CoinReviewDto>>> =
                NetworkRepository.getMarketReview()
            withContext(Dispatchers.Main) {
                when (result) {
                    is ResponseWrapper.Success -> {
                        data.addAll(result.value.data)
                        data.removeIf { it.marketCapUsd == 0.0f }
                        data.sortByDescending { it.marketCapUsd }
                        recyclerData.addAll(data)
                        Loher.d(data.find { it.priceUsd == 0.0f }.toString())
                        setRecycler()
                        realTimeUpdateJob = startRealtimeUpdate()
                    }

                    is ResponseWrapper.NetworkError -> setError()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        data.clear()
        recyclerData.clear()
        realTimeUpdateJob.cancel()
    }

    private fun startAssetReviewIntent(coin: CoinReviewDto) {
        val intent = Intent(requireContext(), AssetReviewActivity::class.java)
        val bundle = Bundle()
        bundle.putString("id", coin.id)
        bundle.putString("name", coin.name)
        bundle.putString("symbol", coin.symbol)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun setLoading() {
        binding.marketReviewRecyclerview.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun setError() {
        binding.marketReviewRecyclerview.isVisible = false
        binding.progressBar.isVisible = false
        binding.networkErrorTv.isVisible = true
        binding.networkErrorIv.isVisible = true
    }

    private fun setRecycler() {
        Log.d("SUCCES", "Грузим данные")
        binding.searchEditText.setAdapter(
            ArrayAdapter(this.requireContext(),
                android.R.layout.simple_expandable_list_item_1,
                recyclerData.map { it.name })
        )
        binding.marketReviewRecyclerview.isVisible = true
        binding.progressBar.isVisible = false
        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.marketReviewRecyclerview.adapter = adapter
    }

    private fun startRealtimeUpdate(): Job =
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                //  if (recyclerData.size != 0 && realTimeUpdateSemaphore.not()) {
                val firstElement =
                    (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastElement =
                    (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                for (i in firstElement..lastElement) {
                    if (recyclerData.size != 0 && realTimeUpdateSemaphore.not()) {
                        when (val result =
                            NetworkRepository.getCoinReview(recyclerData[i].id)) {
                            is ResponseWrapper.Success -> {
                                data[i] = result.value.data.toCoinReviewDto()
                                recyclerData[i] = result.value.data.toCoinReviewDto()
                                withContext(Dispatchers.Main) {
                                    adapter.notifyItemChanged(i)
                                }
                            }

                            is ResponseWrapper.NetworkError -> setError()
                        }
                    }
                }
                //}
            }
        }

}