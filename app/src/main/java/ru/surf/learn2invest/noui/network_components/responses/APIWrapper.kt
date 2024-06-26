package ru.surf.learn2invest.noui.network_components.responses

/**
 * Docs?
 */
data class APIWrapper<T>(
    val data: T,
    val timestamp: Long
)