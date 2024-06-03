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
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                recyclerData.sortByDescending { it.priceUsd }
                adapter.notifyDataSetChanged()
            }
            textInputLayout.setEndIconOnClickListener {
                textView2.visibility = GONE
                clearTv.visibility = GONE
                filterByPrice.visibility = VISIBLE
                filterByMarketcap.visibility = VISIBLE
                filterByChangePercent24Hr.visibility = VISIBLE
                searchEditText.text.clear()
                recyclerData.clear()
                recyclerData.addAll(data)
                adapter.notifyDataSetChanged()
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
            }
            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                textView2.visibility = VISIBLE
                clearTv.visibility = VISIBLE
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

            searchEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
                    true
                } else false
            }
        }
        setLoading()

        this.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                var result: ResponseWrapper<APIWrapper<List<CoinReviewResponse>>> = coinClient.getMarketReview()
                withContext(Dispatchers.Main){
                    when (result) {
                        is ResponseWrapper.Success -> {
                            recyclerData.addAll(result.value.data)
                            data.addAll(result.value.data)
                            setRecycler()
                        }
                        is ResponseWrapper.NetworkError -> setError()
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        //setLoading()
        //setError()
    }
    private fun setLoading () {
        binding.marketReviewRecyclerview.visibility = GONE
        binding.progressBar.visibility = VISIBLE
    }
    private fun setError () {
        binding.marketReviewRecyclerview.visibility = GONE
        binding.progressBar.visibility = GONE
        binding.networkErrorTv.visibility = VISIBLE
        binding.networkErrorIv.visibility = VISIBLE
    }
    private fun setRecycler () {
        Log.d("SUCCES", "Грузим данные")
        binding.searchEditText.setAdapter(ArrayAdapter(this.requireContext(),
            android.R.layout.simple_expandable_list_item_1,
            recyclerData.map { it.name }))
        binding.marketReviewRecyclerview.visibility = VISIBLE
        binding.progressBar.visibility = GONE
        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.marketReviewRecyclerview.adapter = adapter
    }
}