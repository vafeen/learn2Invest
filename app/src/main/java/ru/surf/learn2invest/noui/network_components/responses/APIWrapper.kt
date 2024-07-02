package ru.surf.learn2invest.noui.network_components.responses

/**
 * Обертка верхнего уровня для парсинга JSON
 */
data class APIWrapper<T>(
    val data: T,
    val timestamp: Long
)