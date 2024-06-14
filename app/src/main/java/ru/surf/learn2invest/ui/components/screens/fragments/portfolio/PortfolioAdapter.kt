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
import ru.surf.learn2invest.network_components.util.Const.API_ICON
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest

class PortfolioAdapter(
    private val clickListener: AssetClickListener
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    var assets: List<AssetInvest> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val asset = assets[position]
        holder.bind(asset)
        holder.itemView.setOnClickListener {
            clickListener.onCoinClick(asset)
        }
    }

    override fun getItemCount(): Int = assets.size

    class PortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val coinIcon: ImageView = itemView.findViewById<ImageView>(R.id.coin_icon)
        private val coinName: TextView = itemView.findViewById<TextView>(R.id.coin_name)
        private val coinSymbol: TextView = itemView.findViewById<TextView>(R.id.coin_symbol)
        private val coinTopNumericInfo: TextView =
            itemView.findViewById<TextView>(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo = itemView.findViewById<TextView>(R.id.coin_bottom_numeric_info)
        lateinit var disposable: Disposable

        fun bind(asset: AssetInvest) {
            coinName.text = asset.name
            coinSymbol.text = asset.symbol
            coinTopNumericInfo.text = "${asset.coinPrice}"

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