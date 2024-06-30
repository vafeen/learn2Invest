package ru.surf.learn2invest.ui.components.screens.fragments.marketreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.network_components.responses.CoinReviewDto
import ru.surf.learn2invest.utils.RetrofitLinks.API_ICON
import java.text.NumberFormat
import java.util.Locale

class MarketReviewAdapter(
    private val clickListener: CoinClickListener
) :
    RecyclerView.Adapter<MarketReviewAdapter.ViewHolder>() {
    var data: List<CoinReviewDto> = listOf()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon = itemView.findViewById<ImageView>(R.id.coin_icon)
        val coinTopTextInfo = itemView.findViewById<TextView>(R.id.coin_name)
        val coinBottomTextInfo = itemView.findViewById<TextView>(R.id.coin_symbol)
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
            coinTopTextInfo.text = if (data[position].name.length > 10)
                StringBuilder(data[position].name).insert(10, '\n')
            else
                data[position].name
            coinBottomTextInfo.text = data[position].symbol
            coinTopNumericInfo.text =
                NumberFormat.getInstance(Locale.US).apply {
                    maximumFractionDigits = 4
                }.format(data[position].priceUsd) + " $"
            if (data[position].changePercent24Hr >= 0) {
                coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.recession))
            coinBottomNumericInfo.text =
                NumberFormat.getInstance(Locale.US).apply {
                    maximumFractionDigits = 2
                }.format(data[position].changePercent24Hr) + " %"
            coinIcon.load(
                data = "$API_ICON${data[position].symbol.lowercase()}.svg",
                imageLoader = App.imageLoader
            )
            {
                placeholder(R.drawable.placeholder)
                error(R.drawable.coin_placeholder)
            }
            itemView.setOnClickListener {
                clickListener.onCoinClick(data[position])
            }
        }
    }

    fun interface CoinClickListener {
        fun onCoinClick(coin: CoinReviewDto)
    }

}