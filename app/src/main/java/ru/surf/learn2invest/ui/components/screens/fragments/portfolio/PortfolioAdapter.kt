package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

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
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.network_components.util.Const.API_ICON
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

class PortfolioAdapter(
    private val clickListener: AssetClickListener
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    var assets: List<AssetInvest> = emptyList()
    var priceChanges: Map<String, Float> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val asset = assets[position]
        holder.bind(asset, priceChanges[asset.symbol] ?: 0f)
        holder.itemView.setOnClickListener {
            clickListener.onCoinClick(asset)
        }
    }

    override fun getItemCount(): Int = assets.size

    class PortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        private val coinName: TextView = itemView.findViewById(R.id.coin_name)
        private val coinSymbol: TextView = itemView.findViewById(R.id.coin_symbol)
        private val coinTopNumericInfo: TextView =
            itemView.findViewById(R.id.coin_top_numeric_info)
        private val coinBottomNumericInfo: TextView =
            itemView.findViewById(R.id.coin_bottom_numeric_info)
        lateinit var disposable: Disposable

        fun bind(asset: AssetInvest, priceChange: Float) {
            coinName.text = asset.name
            coinSymbol.text = asset.symbol
            coinTopNumericInfo.text = "$priceChange$"
            val priceChangePercent = ((priceChange - asset.coinPrice) / asset.coinPrice) * 100
            val roundedPercent = BigDecimal(priceChangePercent.toString()).setScale(2, RoundingMode.HALF_UP).toFloat()
            val formattedChange = String.format(Locale.getDefault(), "%.2f%%", priceChangePercent)
            coinBottomNumericInfo.setTextColor(
                when {
                    roundedPercent > 0 -> {
                        coinBottomNumericInfo.text = "+$formattedChange"
                        itemView.context.getColor(R.color.increase)
                    }

                    roundedPercent < 0 -> {
                        coinBottomNumericInfo.text = formattedChange
                        itemView.context.getColor(R.color.recession)
                    }

                    else -> {
                        coinBottomNumericInfo.text = formattedChange
                        itemView.context.getColor(R.color.black)
                    }
                }
            )

            val imageLoader = ImageLoader.Builder(coinIcon.context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
            val request = ImageRequest.Builder(coinIcon.context)
                .data("$API_ICON${asset.symbol.lowercase()}.svg")
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
        }
    }

    override fun onViewRecycled(holder: PortfolioViewHolder) {
        super.onViewRecycled(holder)
        holder.disposable.dispose()
    }

    fun interface AssetClickListener {
        fun onCoinClick(coin: AssetInvest)
    }
}