package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.Learn2InvestApp
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.network_components.responses.APIWrapper
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.logs.Loher


class MarketReviewFragment : Fragment() {
    private var _binding: FragmentMarketReviewBinding? = null
    private val binding get() = _binding!!
    private var recyclerData = mutableListOf<CoinReviewResponse>()
    private var data = mutableListOf<CoinReviewResponse>()
    private val coinClient = NetworkRepository()
    private val adapter = MarketReviewAdapter(recyclerData)
    private var filterByPriceFLag = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
            filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
            filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
            filterByMarketcap.setOnClickListener {
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.marketCapUsd }
                adapter.notifyDataSetChanged()
            }
            filterByChangePercent24Hr.setOnClickListener {
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                recyclerData.sortByDescending { it.changePercent24Hr }
                adapter.notifyDataSetChanged()
            }
            filterByPrice.setOnClickListener {
                filterByPriceFLag = filterByPriceFLag.not()
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                if (filterByPriceFLag) {
                    recyclerData.sortByDescending { it.priceUsd }
                    Loher.d(recyclerData.find{ it.symbol == "CJ" }.toString())
                    filterByPrice.setIconResource(R.drawable.arrow_bottom_red)
                    filterByPrice.setIconTintResource(R.color.recession)
                }
                else {
                    recyclerData.sortBy { it.priceUsd }
                    filterByPrice.setIconResource(R.drawable.arrow_top_green)
                    filterByPrice.setIconTintResource(R.color.label)
                }
                adapter.notifyDataSetChanged()
            }
            textInputLayout.setEndIconOnClickListener {
                textView2.isVisible = false
                clearTv.isVisible = false
                filterByPrice.isVisible = true
                filterByMarketcap.isVisible = true
                filterByChangePercent24Hr.isVisible = true
                searchEditText.text.clear()
                recyclerData.clear()
                recyclerData.addAll(data.sortedByDescending{it.marketCapUsd})
                adapter.notifyDataSetChanged()
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
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
                    Learn2InvestApp.mainDB.searchedCoinDao().deleteAll()
                    withContext(Dispatchers.Main) {
                        recyclerData.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            searchEditText.setOnItemClickListener { parent, view, position, id ->
                var searchedList = mutableListOf<String>()
                lifecycleScope.launch(Dispatchers.IO) {
                    Learn2InvestApp.mainDB
                        .searchedCoinDao()
                        .insertAll(
                            SearchedCoin(coinID = searchEditText.text.toString())
                        )
                    Learn2InvestApp.mainDB
                        .searchedCoinDao()
                        .getAllAsList().map { searchedList.add(it.coinID) }
                    withContext(Dispatchers.Main){
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
                var result: ResponseWrapper<APIWrapper<List<CoinReviewResponse>>> = coinClient.getMarketReview()
                withContext(Dispatchers.Main){
                    when (result) {
                        is ResponseWrapper.Success -> {
                            data.addAll(result.value.data)
                            data.removeIf { it.marketCapUsd == 0.0 }
                            recyclerData.addAll(data)
                            Loher.d(data.find { it.priceUsd == 0.0}.toString())
                            setRecycler()
                        }
                        is ResponseWrapper.NetworkError -> setError()
                    }
                }
        }
    }
    private fun setLoading () {
        binding.marketReviewRecyclerview.isVisible = false
        binding.progressBar.isVisible = true
    }
    private fun setError () {
        binding.marketReviewRecyclerview.isVisible = false
        binding.progressBar.isVisible = false
        binding.networkErrorTv.isVisible = true
        binding.networkErrorIv.isVisible = true
    }
    private fun setRecycler () {
        Log.d("SUCCES", "Грузим данные")
        binding.searchEditText.setAdapter(ArrayAdapter(this.requireContext(),
            android.R.layout.simple_expandable_list_item_1,
            recyclerData.map { it.name }))
        binding.marketReviewRecyclerview.isVisible = true
        binding.progressBar.isVisible = false
        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.marketReviewRecyclerview.adapter = adapter
    }
}