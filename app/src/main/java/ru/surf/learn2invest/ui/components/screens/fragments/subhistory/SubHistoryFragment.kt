package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentAssetHistoryBinding
import ru.surf.learn2invest.noui.database_components.entity.Transaction

class SubHistoryFragment: Fragment() {
    private val binding by lazy { FragmentAssetHistoryBinding.inflate(layoutInflater) }
    private val data = mutableListOf<Transaction>()
    private var symbol: String? = null
    private val adapter = SubHistoryAdapter(data)
    override fun onStart() {
        super.onStart()
        symbol = arguments?.getString("SYMBOL") //TODO Определиться где будут все константы
        binding.assetHistory.layoutManager = LinearLayoutManager(this.requireContext())
        binding.assetHistory.adapter = adapter
        if (symbol.isNullOrBlank().not())
            lifecycleScope.launch(Dispatchers.IO) {
                App.mainDB.transactionDao().getFilteredBySymbol(symbol!!).collect{
                    data.addAll(it)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }
}