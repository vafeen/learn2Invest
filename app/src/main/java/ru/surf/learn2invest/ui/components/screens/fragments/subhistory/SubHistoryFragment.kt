package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.FragmentAssetHistoryBinding
import ru.surf.learn2invest.utils.AssetConstants
import ru.surf.learn2invest.utils.viewModelCreator
import javax.inject.Inject

/**
 * Фрагмент истории сделок с одним активом в [AssetReviewActivity][ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity]
 */
@AndroidEntryPoint
class SubHistoryFragment : Fragment() {
    private lateinit var binding: FragmentAssetHistoryBinding

    @Inject
    lateinit var factory: SubHistoryFragmentViewModel.Factory

    private val viewModel: SubHistoryFragmentViewModel by viewModelCreator {
        factory.createSubHistoryAssetViewModel(
            symbol = requireArguments().getString(AssetConstants.SYMBOL.key) ?: ""
        )
    }

    @Inject
    lateinit var adapter: SubHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetHistoryBinding.inflate(inflater, container, false)

        binding.assetHistory.layoutManager = LinearLayoutManager(this.requireContext())
        binding.assetHistory.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.data.collect {
                if (it.isEmpty()) {
                    binding.assetHistory.isVisible = false
                    binding.noActionsError.isVisible = true
                } else {
                    adapter.data = it
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        fun newInstance(symbol: String): SubHistoryFragment {
            val fragment = SubHistoryFragment()
            val args = Bundle()
            args.putString(AssetConstants.SYMBOL.key, symbol)
            fragment.arguments = args
            return fragment
        }
    }
}