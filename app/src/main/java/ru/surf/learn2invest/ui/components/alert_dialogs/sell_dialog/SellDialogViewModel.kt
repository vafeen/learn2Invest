package ru.surf.learn2invest.ui.components.alert_dialogs.sell_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import ru.surf.learn2invest.noui.database_components.entity.transaction.TransactionsType
import ru.surf.learn2invest.noui.network_components.NetworkRepository
import ru.surf.learn2invest.noui.network_components.responses.ResponseWrapper
import ru.surf.learn2invest.utils.getWithCurrency

class SellDialogViewModel : ViewModel() {
    lateinit var realTimeUpdateJob: Job
    lateinit var coin: AssetInvest

    fun sell(price: Float, amountCurrent: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseRepository.apply {
                // обновление баланса
                updateProfile(
                    profile.copy(fiatBalance = profile.fiatBalance + price * amountCurrent)
                )
                coin.apply {
                    // обновление истории
                    insertAllTransaction(
                        Transaction(
                            coinID = assetID,
                            name = name,
                            symbol = symbol,
                            coinPrice = price,
                            dealPrice = price * amountCurrent,
                            amount = amountCurrent,
                            transactionType = TransactionsType.Sell
                        )
                    )
                }
                // обновление портфеля
                if (amountCurrent < coin.amount) {
                    insertAllAssetInvest(
                        coin.copy(
                            coinPrice = (coin.coinPrice * coin.amount - amountCurrent * price) / (coin.amount - amountCurrent),
                            amount = coin.amount - amountCurrent
                        )
                    )
                } else deleteAssetInvest(coin)
            }
        }
    }

    fun startRealTimeUpdate(onUpdateFields: (result: String) -> Unit): Job =
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                when (val result = NetworkRepository.getCoinReview(coin.assetID)) {
                    is ResponseWrapper.Success -> {
                        onUpdateFields(result.value.priceUsd.getWithCurrency())
                    }

                    is ResponseWrapper.NetworkError -> {}
                }

                delay(5000)
            }
        }
}