package ru.surf.learn2invest.ui.components.screens.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.databinding.FragmentHistoryBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction
import ru.surf.learn2invest.noui.logs.Loher

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val data = mutableListOf<Transaction>()
    private val adapter = HistoryAdapter(data)
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
            DatabaseRepository.getAllAsFlowTransaction().collect {
                Loher.d(it.size.toString())
                if (it.isEmpty()) {
                    binding.historyRecyclerview.isVisible = false
                    binding.noActionsTv.isVisible = true
                } else {
                    data.addAll(it)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        data.clear()
    }
}