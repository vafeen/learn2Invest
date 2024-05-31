package ru.surf.learn2invest.ui.portfolio.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.surf.learn2invest.databinding.FragmentPortfolioBinding

class PortfolioFragment : Fragment() {
    private lateinit var binding: FragmentPortfolioBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        return binding.root
    }
}