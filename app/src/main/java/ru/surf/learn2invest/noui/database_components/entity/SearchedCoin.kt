package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Coin'ы из поисковых запросов
 */

@Entity
data class SearchedCoin(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val coinID: String //Имя коина (Bitcoin)
)


