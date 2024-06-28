package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewDto
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity

/**
 * Фрагмент обзора рынка в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
class MarketReviewFragment : Fragment() {
    private val binding by lazy { FragmentMarketReviewBinding.inflate(layoutInflater) }
    private val viewModel: MarketReviewFragmentViewModel by viewModels()
    private val adapter = MarketReviewAdapter() { coin ->
        startAssetReviewIntent(coin)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.marketReviewRecyclerview.adapter = adapter

        lifecycleScope.launch {
            viewModel.filterOrder.collect {
                binding.apply {
                    if (it) {
                        filterByPrice.setIconResource(R.drawable.arrow_top_green)
                        filterByPrice.setIconTintResource(R.color.label)
                    } else {
                        filterByPrice.setIconResource(R.drawable.arrow_bottom_red)
                        filterByPrice.setIconTintResource(R.color.recession)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.apply {
                    marketReviewRecyclerview.isVisible = it.not()
                    binding.progressBar.isVisible = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isError.collect {
                binding.apply {
                    marketReviewRecyclerview.isVisible = it.not()
                    networkErrorTv.isVisible = it
                    networkErrorIv.isVisible = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.searchedData.collect {
                if (adapter.data.size != it.size) {
                    adapter.data = it
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.data = it
                    if (viewModel.isRealtimeUpdate) {
                        adapter.notifyItemRangeChanged(
                            viewModel.firstUpdateElement,
                            viewModel.amountUpdateElement
                        )
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.data.collect {
                if (it.isNotEmpty()) {
                    adapter.data = it
                    if (viewModel.isRealtimeUpdate) {
                        adapter.notifyItemRangeChanged(
                            viewModel.firstUpdateElement,
                            viewModel.amountUpdateElement
                        )
                    } else {
                        adapter.notifyDataSetChanged()
                        binding.searchEditText.setAdapter(
                            ArrayAdapter(this@MarketReviewFragment.requireContext(),
                                android.R.layout.simple_expandable_list_item_1,
                                it.map { element -> element.name })
                        )
                        Log.d("data.collect", "startRealtimeUpdate")
                        startRealtimeUpdate()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filterState.collect {
                binding.apply {
                    filterByMarketcap.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_MARKETCAP] == true)
                                    R.color.main_background
                                else
                                    R.color.view_background
                            )
                        )
                    filterByChangePercent24Hr.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_PERCENT] == true)
                                    R.color.main_background
                                else R.color.view_background
                            )
                        )
                    filterByPrice.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_PRICE] == true)
                                    R.color.main_background
                                else
                                    R.color.view_background
                            )
                        )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isSearch.collect {
                binding.apply {
                    textView2.isVisible = it
                    clearTv.isVisible = it
                    filterByPrice.isVisible = it.not()
                    filterByMarketcap.isVisible = it.not()
                    filterByChangePercent24Hr.isVisible = it.not()
                    searchEditText.text.clear()
                    if (it) searchEditText.hint = ""
                    if (it.not()) {
                        adapter.data = viewModel.data.value
                    } else adapter.data = viewModel.data.value
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.apply {
            filterByMarketcap.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByMarketcap()
            }

            filterByChangePercent24Hr.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByPercent()
            }

            filterByPrice.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByPrice()
            }

            textInputLayout.setEndIconOnClickListener {
                viewModel.setSearchState(false)
                searchEditText.clearFocus()
            }

            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) viewModel.setSearchState(true)
            }

            searchEditText.setOnItemClickListener { parent, view, position, id ->
                viewModel.setSearchState(true, searchEditText.text.toString())
            }

            clearTv.setOnClickListener {
                viewModel.clearSearchData()
            }

        }
        return binding.root
    }

    private fun startRealtimeUpdate() = lifecycleScope.launch {
        while (true) {
            delay(10000)
            val firstElement =
                (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastElement =
                (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            viewModel.updateData(firstElement, lastElement)
        }
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

    companion object {
        const val FILTER_BY_MARKETCAP = 0
        const val FILTER_BY_PERCENT = 1
        const val FILTER_BY_PRICE = 2
    }
}