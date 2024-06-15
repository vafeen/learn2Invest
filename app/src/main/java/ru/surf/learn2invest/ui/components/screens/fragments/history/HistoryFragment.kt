package ru.surf.learn2invest.ui.components.screens.fragments.history

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
import ru.surf.learn2invest.databinding.FragmentHistoryBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.logs.Loher

class HistoryFragment : Fragment() {


	private lateinit var binding: FragmentHistoryBinding
	private var data = listOf<Transaction>()
	private val adapter = HistoryAdapter(data)
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
			DatabaseRepository.getAllAsFlowTransaction().collect {
				Loher.d(it.size.toString())
				data = it
				withContext(Dispatchers.Main) {
					adapter.notifyDataSetChanged()
				}
			}
		}
	}
}