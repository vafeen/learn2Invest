package ru.surf.learn2invest.ui.alert_dialogs.price_alert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.PriceAlertDialogBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert

class PriceAlertAdapter(
    data: List<PriceAlert>, val context: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val mainBinding: PriceAlertDialogBinding,
    val onUpdateItems: () -> Unit
) :
    RecyclerView.Adapter<PriceAlertAdapter.ViewHolder>() {

    var alerts = data

    fun enableOrDisableUpdatingAlertDataByAvailabilityCurrentAlertIndex() {
        mainBinding.apply {
            buttonCreatePriceAlertPriceAlertDialog.text = if (PriceAlertDialog.currentAlert != null) {
                "Обновить"
            } else {
                "Добавить"
            }
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private fun changeTextFieldsInfo(alert: PriceAlert?) {

            mainBinding.apply {

                pricePriceAlertDialog.setText("${alert?.coinPrice ?: ""}")

                priceInPercentPriceAlertDialog.setText("${alert?.changePercent24Hr ?: ""}")

                commentAlertDialog.setText(alert?.comment ?: "")
            }

        }

        private fun updateItem(position: Int) {
            view.findViewById<CardView>(R.id.full_alert_item).apply {
                if (PriceAlertDialog.currentAlert == position) {
                    setCardBackgroundColor(
                        ContextCompat.getColorStateList(context, R.color.increase)
                    )
                } else {
                    setCardBackgroundColor(
                        ContextCompat.getColorStateList(context, R.color.view_background)
                    )
                }
            }
        }

        fun bindData(alert: PriceAlert, position: Int) {

            updateItem(position = position)

            view.findViewById<TextView>(R.id.textView_alert_item).text = alert.let {
                "${it.coinPrice} Р / ${it.changePercent24Hr} %"
            }
            view.findViewById<ImageButton>(R.id.remove_alert_item).setOnClickListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    Learn2InvestApp.mainDB.priceAlertDao().delete(alert)
                }

            }

            view.findViewById<CardView>(R.id.full_alert_item).apply { ->
                setOnClickListener {
                    if (position != PriceAlertDialog.currentAlert) {
                        PriceAlertDialog.currentAlert = position
                    } else {
                        PriceAlertDialog.currentAlert = null
                    }
                    updateItem(position = position)

                    onUpdateItems()

                    changeTextFieldsInfo(alert = PriceAlertDialog.currentAlert?.let {
                        alerts[it]
                    })

                    enableOrDisableUpdatingAlertDataByAvailabilityCurrentAlertIndex()
                }

            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.alert_item, viewGroup, false)

        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.bindData(alerts[position], position)
    }

    override fun getItemCount(): Int = alerts.size

}