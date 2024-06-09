package ru.surf.learn2invest.ui.components.screens.trading_password

enum class TradingPasswordActivityActions(
    val action: String,
    val actionName: String,
    val mainButtonAction: String
) {
    CreateTradingPassword(
        action = CreateTradingPassword.name,
        actionName = "Создание торгового пароля",
        mainButtonAction = "Создать"
    ),
    ChangeTradingPassword(
        action = ChangeTradingPassword.name,
        actionName = "Изменение торгового пароля",
        mainButtonAction = "Изменить"
    ),
    RemoveTradingPassword(
        action = RemoveTradingPassword.name,
        actionName = "Удаление торгового пароля",
        mainButtonAction = "Удалить"
    );
}