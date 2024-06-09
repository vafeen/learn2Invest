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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.network_components.responses.APIWrapper
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity


class MarketReviewFragment : Fragment() {
    private val binding by lazy { FragmentMarketReviewBinding.inflate(layoutInflater) }
    private var recyclerData = mutableListOf<CoinReviewResponse>()
    private var data = mutableListOf<CoinReviewResponse>()
    private val coinClient = NetworkRepository()
    private val adapter = MarketReviewAdapter(recyclerData) { coin ->
        startAssetReviewIntent(coin)
    }
    private var filterByPriceFLag = false
    private var filterByPriceIsFirstActive = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                filterByPriceIsFirstActive = true
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.marketCapUsd }
                adapter.notifyDataSetChanged()
            }
            filterByChangePercent24Hr.setOnClickListener {
                filterByPriceIsFirstActive = true
                filterByMarketcap.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByPrice.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.changePercent24Hr }
                adapter.notifyDataSetChanged()
            }
            filterByPrice.setOnClickListener {
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
            }
            textInputLayout.setEndIconOnClickListener {
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
            }
            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                textView2.isVisible = true
                clearTv.isVisible = true
                filterByPrice.visibility = INVISIBLE
                filterByMarketcap.visibility = INVISIBLE
                filterByChangePercent24Hr.visibility = INVISIBLE
                searchEditText.hint = ""
            }

            clearTv.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    App.mainDB.searchedCoinDao().deleteAll()
                    withContext(Dispatchers.Main) {
                        recyclerData.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            searchEditText.setOnItemClickListener { parent, view, position, id ->
                var searchedList = mutableListOf<String>()
                lifecycleScope.launch(Dispatchers.IO) {
                    App.mainDB
                        .searchedCoinDao()
                        .insertAll(
                            SearchedCoin(coinID = searchEditText.text.toString())
                        )
                    App.mainDB
                        .searchedCoinDao()
                        .getAll().first().map { searchedList.add(it.coinID) }
                    withContext(Dispatchers.Main) {
                        recyclerData.clear()
                        recyclerData.addAll(data.filter { searchedList.contains(it.name) })
                        recyclerData.reverse()
                        adapter.notifyDataSetChanged()
                        Loher.d(searchedList.toString())
                    }
                }
            }
        }
        setLoading()

        this.lifecycleScope.launch(Dispatchers.IO) {
            var result: ResponseWrapper<APIWrapper<List<CoinReviewResponse>>> =
                coinClient.getMarketReview()
            withContext(Dispatchers.Main) {
                when (result) {
                    is ResponseWrapper.Success -> {
                        data.addAll(result.value.data)
                        data.removeIf { it.marketCapUsd == 0.0 }
                        recyclerData.addAll(data)
                        Loher.d(data.find { it.priceUsd == 0.0 }.toString())
                        setRecycler()
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
    }

    private fun startAssetReviewIntent(coin: CoinReviewResponse) {
        val playerIntent = Intent(requireContext(), AssetReviewActivity::class.java)
        playerIntent.putExtra("symbol", coin.symbol)
        startActivity(playerIntent)
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
}