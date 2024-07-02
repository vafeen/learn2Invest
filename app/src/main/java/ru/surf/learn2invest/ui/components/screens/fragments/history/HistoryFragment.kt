package ru.surf.learn2invest.ui.components.screens.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.surf.learn2invest.utils.setStatusBarColor
import javax.inject.Inject

/**
 * Фрагмент истории сделок в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryFragmentViewModel by viewModels()

    @Inject
    lateinit var adapter: HistoryFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.apply {
            setStatusBarColor(
                window,
                this,
                R.color.accent_background,
                R.color.accent_background_dark
            )
        }

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
}