package ru.surf.learn2invest.ui.asset_review.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.surf.learn2invest.databinding.FragmentAssetReviewBinding

class AssetReviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetReviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}