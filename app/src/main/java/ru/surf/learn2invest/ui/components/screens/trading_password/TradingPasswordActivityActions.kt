package ru.surf.learn2invest.ui.components.screens.trading_password

enum class TradingPasswordActivityActions(
    val action: String
) {
    CreateTradingPassword(action = CreateTradingPassword.name),
    ChangeTradingPassword(action = ChangeTradingPassword.name),
    RemoveTradingPassword(action = RemoveTradingPassword.name);
}
