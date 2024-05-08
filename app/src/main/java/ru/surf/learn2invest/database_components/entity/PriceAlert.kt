package ru.surf.learn2invest.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey




// для сохрарнения необходимых оповещений

@Entity
class PriceAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val coinPrice: Int,
    val changePercent24Hr: Int
)