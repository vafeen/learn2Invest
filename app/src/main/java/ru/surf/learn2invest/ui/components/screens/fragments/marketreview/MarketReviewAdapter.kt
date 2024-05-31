package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import ru.surf.learn2invest.R
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse

class MarketReviewAdapter(private val data: List<CoinReviewResponse>) : RecyclerView.Adapter<MarketReviewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon = itemView.findViewById<ImageView>(R.id.coin_icon)
        val coinTopTextInfo = itemView.findViewById<TextView>(R.id.coin_top_text_info)
        val coinBottomTextInfo = itemView.findViewById<TextView>(R.id.coin_bottom_text_info)
        val coinTopNumericInfo = itemView.findViewById<TextView>(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo = itemView.findViewById<TextView>(R.id.coin_bottom_numeric_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.coin_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            coinTopTextInfo.text = data[position].name
            coinBottomTextInfo.text = data[position].symbol
            coinTopNumericInfo.text = data[position].priceUsd.toString()
            coinBottomNumericInfo.text = data[position].changePercent24Hr.toString()
//            coinIcon.load("https://cryptofonts.com/img/icons/${data[position].symbol.lowercase()}.svg") {
//                decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
//            }
            val imageLoader = ImageLoader.Builder(coinIcon.context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
            val request = ImageRequest.Builder(coinIcon.context)
                .data("https://cryptofonts.com/img/icons/${data[position].symbol.lowercase()}.svg")
                .target(onSuccess = {
                    coinIcon.setImageDrawable(it)
                },
                    onError = {
                        coinIcon.setImageResource(R.drawable.coin_placeholder)
                    },
                    onStart = {
                        coinIcon.setImageResource(R.drawable.coin_placeholder)
                    })
                .build()
            imageLoader.enqueue(request)
        }
    }
}