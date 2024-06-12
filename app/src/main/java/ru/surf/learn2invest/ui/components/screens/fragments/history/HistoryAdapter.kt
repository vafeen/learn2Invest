package ru.surf.learn2invest.ui.components.screens.fragments.history

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
import ru.surf.learn2invest.network_components.util.Const
import ru.surf.learn2invest.noui.database_components.entity.Transaction
import ru.surf.learn2invest.noui.database_components.entity.TransactionsType
import ru.surf.learn2invest.noui.logs.Loher

class HistoryAdapter(private val data: List<Transaction>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon = itemView.findViewById<ImageView>(R.id.coin_icon)
        val coinTopTextInfo = itemView.findViewById<TextView>(R.id.coin_name)
        val coinBottomTextInfo = itemView.findViewById<TextView>(R.id.coin_symbol)
        val coinTopNumericInfo = itemView.findViewById<TextView>(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo = itemView.findViewById<TextView>(R.id.coin_bottom_numeric_info)
        lateinit var disposable: Disposable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.coin_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            coinTopTextInfo.text = if (data[position].name.length > 12)
                "${data[position].name.substring(0..12)}..."
            else
                data[position].name
            coinBottomTextInfo.text = data[position].symbol
            if (data[position].transactionType == TransactionsType.Sell) {
                coinTopNumericInfo.text = "+ ${data[position].coinPrice}$"
                coinTopNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else {
                coinTopNumericInfo.text = "- ${data[position].coinPrice}$"
            }
            coinBottomNumericInfo.text = "${data[position].dealPrice}$"

            val imageLoader = ImageLoader.Builder(coinIcon.context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
            val request = ImageRequest.Builder(coinIcon.context)
                .data("${Const.API_ICON}${data[position].symbol.lowercase()}.svg")
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
                Loher.d(data[position].coinPrice.toString())
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.disposable.dispose()
    }
}