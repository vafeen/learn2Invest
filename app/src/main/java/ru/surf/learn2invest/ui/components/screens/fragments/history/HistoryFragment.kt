package ru.surf.learn2invest.ui.components.screens.fragments.history

import android.content.Intent
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
import ru.surf.learn2invest.databinding.FragmentHistoryBinding
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val data = mutableListOf<Transaction>()
    private val adapter = HistoryAdapter(data) { transaction ->
        startAssetReviewIntent(transaction)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.historyRecyclerview.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            App.mainDB.transactionDao().getAllAsFlow().collect {
                Loher.d(it.size.toString())
                data.addAll(it)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
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
    override fun onStop() {
        super.onStop()
        data.clear()
    }
}