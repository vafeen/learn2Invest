package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.databinding.FragmentAssetHistoryBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction

class SubHistoryFragment : Fragment() {
    private lateinit var binding: FragmentAssetHistoryBinding
    private lateinit var viewModel: SubHistoryViewModel
    private val adapter = SubHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetHistoryBinding.inflate(inflater, container, false)
        val symbol =
            requireArguments().getString("symbol") ?: ""
        viewModel = SubHistoryViewModel(symbol)
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
            args.putString("symbol", symbol)
            fragment.arguments = args
            return fragment
        }
    }
}