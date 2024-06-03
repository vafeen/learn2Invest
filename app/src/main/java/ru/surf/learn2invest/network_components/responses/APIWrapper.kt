package ru.surf.learn2invest.network_components.responses

data class APIWrapper <T> (
    val data: T,
    val timestamp: Long
)