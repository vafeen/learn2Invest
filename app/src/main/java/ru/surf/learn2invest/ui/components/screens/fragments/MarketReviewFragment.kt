package ru.surf.learn2invest.ui.components.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding

// Экран Обзор рынка
class MarketReviewFragment : Fragment() {
    private lateinit var binding: FragmentMarketReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarketReviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}