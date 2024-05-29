package ru.surf.learn2invest.ui.market_review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.surf.learn2invest.databinding.FragmentMarketReviewBinding

class MarketReviewFragment : Fragment() {
    private var _binding: FragmentMarketReviewBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketReviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}