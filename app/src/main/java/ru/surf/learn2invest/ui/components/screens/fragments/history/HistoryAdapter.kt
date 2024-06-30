package ru.surf.learn2invest.ui.components.screens.fragments.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.transaction.TransactionsType
import ru.surf.learn2invest.utils.RetrofitLinks.API_ICON

class HistoryAdapter(
    private val clickListener: HistoryClickListener
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var data: List<Transaction> = listOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        val coinTopTextInfo: TextView = itemView.findViewById(R.id.coin_name)
        val coinBottomTextInfo: TextView = itemView.findViewById(R.id.coin_symbol)
        val coinTopNumericInfo: TextView = itemView.findViewById(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo: TextView = itemView.findViewById(R.id.coin_bottom_numeric_info)
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
            } else coinTopNumericInfo.text = "- ${data[position].coinPrice}$"
            coinBottomNumericInfo.text = "${data[position].dealPrice}$"

            coinIcon.load(
                data = "${API_ICON}${data[position].symbol.lowercase()}.svg",
                imageLoader = App.imageLoader
            )
            itemView.setOnClickListener {
                clickListener.onCoinClick(data[position])
            }
        }
    }

    fun interface HistoryClickListener {
        fun onCoinClick(coin: Transaction)
    }

}