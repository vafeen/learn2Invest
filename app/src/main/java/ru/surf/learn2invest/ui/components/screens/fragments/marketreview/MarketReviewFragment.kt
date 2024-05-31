package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.network_components.NetworkRepository
import ru.surf.learn2invest.network_components.ResponseWrapper
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.network_components.responses.MarketReviewResponse

class MarketReviewFragment : Fragment() {
    private var _binding: FragmentMarketReviewBinding? = null
    private val binding get() = _binding!!
    private var data = mutableListOf<CoinReviewResponse>()
    private val coinClient = NetworkRepository()
    private val adapter = MarketReviewAdapter(data)
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
                data.sortByDescending { it.marketCapUsd }
                adapter.notifyDataSetChanged()
            }
            filterByChangePercent24Hr.setOnClickListener {
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                data.sortByDescending { it.changePercent24Hr }
                adapter.notifyDataSetChanged()
            }
            filterByPrice.setOnClickListener {
                filterByMarketcap.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByChangePercent24Hr.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.view_background))
                filterByPrice.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_background))
                data.sortByDescending { it.priceUsd }
                adapter.notifyDataSetChanged()
            }
        }
        setLoading()
        lateinit var result: ResponseWrapper<MarketReviewResponse>
        this.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                result = coinClient.getMarketReview()
            }
            when (result) {
                is ResponseWrapper.Success -> {
                    data.addAll((result as ResponseWrapper.Success<MarketReviewResponse>).value.data)
                    setRecycler()
                }
                is ResponseWrapper.NetworkError -> setError()
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
        binding.marketReviewRecyclerview.visibility = VISIBLE
        binding.progressBar.visibility = GONE
        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.context)
        binding.marketReviewRecyclerview.adapter = adapter
    }
}