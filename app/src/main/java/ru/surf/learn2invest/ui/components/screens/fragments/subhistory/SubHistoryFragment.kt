package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentAssetHistoryBinding
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction

class SubHistoryFragment : Fragment() {
    private lateinit var binding: FragmentAssetHistoryBinding
    private val data = mutableListOf<Transaction>()
    private var symbol: String? = null
    private val adapter = SubHistoryAdapter(data)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetHistoryBinding.inflate(inflater, container, false)
        symbol = requireArguments().getString("symbol") //TODO Определиться где будут все константы
        binding.assetHistory.layoutManager = LinearLayoutManager(this.requireContext())
        binding.assetHistory.adapter = adapter
        if (symbol.isNullOrBlank().not())
            lifecycleScope.launch(Dispatchers.IO) {
                App.mainDB.transactionDao().getFilteredBySymbol(symbol!!).collect {
                    data.addAll(it)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        return binding.root
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