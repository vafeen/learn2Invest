package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import ru.surf.learn2invest.R
import ru.surf.learn2invest.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.network_components.util.Const.API_ICON
import ru.surf.learn2invest.network_components.util.round

class MarketReviewAdapter(private val data: List<CoinReviewResponse>) :
    RecyclerView.Adapter<MarketReviewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon = itemView.findViewById<ImageView>(R.id.coin_icon)
        val coinTopTextInfo = itemView.findViewById<TextView>(R.id.coin_top_text_info)
        val coinBottomTextInfo = itemView.findViewById<TextView>(R.id.coin_bottom_text_info)
        val coinTopNumericInfo = itemView.findViewById<TextView>(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo = itemView.findViewById<TextView>(R.id.coin_bottom_numeric_info)
        lateinit var disposable: Disposable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.coin_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            coinTopTextInfo.text = if (data[position].name.length > 10)
                StringBuilder(data[position].name).insert(10, '\n')
            else
                data[position].name
            coinBottomTextInfo.text = data[position].symbol
            coinTopNumericInfo.text = "\$${data[position].priceUsd.round()}"
            if (data[position].changePercent24Hr >= 0) {
                coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.recession))
            coinBottomNumericInfo.text = "${data[position].changePercent24Hr.round() ?: 0}%"

            val imageLoader = ImageLoader.Builder(coinIcon.context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
            val request = ImageRequest.Builder(coinIcon.context)
                .data("$API_ICON${data[position].symbol.lowercase()}.svg")
                .target(onSuccess = {
                    coinIcon.setImageDrawable(it)
                },
                    onError = {
                        coinIcon.setImageResource(R.drawable.coin_placeholder)
                    },
                    onStart = {
                        coinIcon.setImageResource(R.drawable.placeholder)
                    })
                .build()
            disposable = imageLoader.enqueue(request)
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(
                    "symbol",
                    data[position].symbol
                ) //TODO Придумать место для констант всего приложения
//                findNavController(itemView).navigate(
//                    R.id.action_marketReviewFragment_to_assetReviewFragment,
//                    bundle
//                )
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.disposable.dispose()
    }
}