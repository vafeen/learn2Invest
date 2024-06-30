package ru.surf.learn2invest.noui.database_components.entity.transaction

/**
 * Класс типов транзакции
 **/

enum class TransactionsType(val action: Int) {
    Buy(action = 1),
    Sell(action = 0)
}