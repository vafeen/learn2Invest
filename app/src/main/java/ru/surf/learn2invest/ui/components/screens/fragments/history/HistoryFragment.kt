package ru.surf.learn2invest.ui.components.screens.fragments.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.FragmentHistoryBinding
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity

/**
 * Фрагмент истории сделок в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryFragmentViewModel by viewModels()
    private val adapter = HistoryAdapter { transaction ->
        startAssetReviewIntent(transaction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.main_background)

        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.historyRecyclerview.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.data.collect {
                if (it.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        binding.historyRecyclerview.isVisible = false
                        binding.noActionsTv.isVisible = true
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        adapter.data = it
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun startAssetReviewIntent(coin: Transaction) {
        val intent = Intent(requireContext(), AssetReviewActivity::class.java)
        val bundle = Bundle()
        bundle.putString("id", coin.coinID)
        bundle.putString("name", coin.name)
        bundle.putString("symbol", coin.symbol)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}