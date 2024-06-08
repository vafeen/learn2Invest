package ru.surf.learn2invest.network_components

sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T) : ResponseWrapper<T>()
    data object NetworkError : ResponseWrapper<Nothing>()
}