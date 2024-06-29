package ru.surf.learn2invest.ui.components.alert_dialogs.buy_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject


@HiltViewModel
class BuyDialogViewModel @Inject constructor(
    var databaseRepository: DatabaseRepository,
    var networkRepository: NetworkRepository
) : ViewModel() {
    lateinit var realTimeUpdateJob: Job
    var haveAssetsOrNot = false
    lateinit var coin: AssetInvest

    fun buy(price: Float, amountCurrent: Float) {
        val balance = databaseRepository.profile.fiatBalance
        if (balance > price * amountCurrent) {
            viewModelScope.launch(Dispatchers.IO) {
                databaseRepository.apply {
                    // обновление баланса
                    updateProfile(profile.copy(fiatBalance = balance - price * amountCurrent))
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
                                transactionType = TransactionsType.Buy
                            )
                        )
                    }
                    // обновление портфеля
                    if (haveAssetsOrNot) {
                        updateAssetInvest(
                            coin.copy(
                                coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                        / (coin.amount + amountCurrent),
                                amount = coin.amount + amountCurrent
                            )
                        )
                    } else {
                        insertAllAssetInvest(
                            coin.copy(
                                coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                        / (coin.amount + amountCurrent),
                                amount = coin.amount + amountCurrent
                            )
                        )
                    }
                }
            }
        }
    }

    fun startRealTimeUpdate(onUpdateFields: (result: String) -> Unit): Job =
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                when (val result = networkRepository.getCoinReview(coin.assetID)) {
                    is ResponseWrapper.Success -> {
                        onUpdateFields(result.value.priceUsd.getWithCurrency())
                    }

                    is ResponseWrapper.NetworkError -> {}
                }
                delay(5000)
            }
        }
}
