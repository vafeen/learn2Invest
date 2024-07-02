package ru.surf.learn2invest.noui.network_components.responses

/**
 * Обертка для описания состояния полученных данных
 */
sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T) : ResponseWrapper<T>()
    data object NetworkError : ResponseWrapper<Nothing>()
}