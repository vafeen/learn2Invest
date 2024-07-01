package ru.surf.learn2invest.ui.components.screens.fragments.portfolio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import dagger.hilt.android.qualifiers.ActivityContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity
import ru.surf.learn2invest.utils.RetrofitLinks.API_ICON
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import javax.inject.Inject


class PortfolioAdapter @Inject constructor(
    private val imageLoader: ImageLoader,
    @ActivityContext var context: Context
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
            context.startActivity(Intent(context, AssetReviewActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(AssetConstants.ID.key, asset.assetID)
                    putString(AssetConstants.NAME.key, asset.name)
                    putString(AssetConstants.SYMBOL.key, asset.symbol)
                })
            })
        }
    }

    override fun getItemCount(): Int = assets.size

    inner class PortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        private val coinName: TextView = itemView.findViewById(R.id.coin_name)
        private val coinSymbol: TextView = itemView.findViewById(R.id.coin_symbol)
        private val coinTopNumericInfo: TextView =
            itemView.findViewById(R.id.coin_top_numeric_info)
        private val coinBottomNumericInfo: TextView =
            itemView.findViewById(R.id.coin_bottom_numeric_info)

        fun bind(asset: AssetInvest, priceChange: Float) {
            coinName.text = asset.name
            coinSymbol.text = asset.symbol
            coinTopNumericInfo.text = "$priceChange$"
            val priceChangePercent = ((priceChange - asset.coinPrice) / asset.coinPrice) * 100
            val roundedPercent =
                BigDecimal(priceChangePercent.toString()).setScale(2, RoundingMode.HALF_UP)
                    .toFloat()
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

            coinIcon.load(
                data = "$API_ICON${asset.symbol.lowercase()}.svg",
                imageLoader = imageLoader
            )
            {
                placeholder(R.drawable.placeholder)
                error(R.drawable.coin_placeholder)
            }
        }
    }
}