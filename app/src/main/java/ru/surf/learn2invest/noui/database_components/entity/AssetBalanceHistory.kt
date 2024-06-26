package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


/**
 * Объект баланса портфеля
 * @param id [Первичный ключ в базе данных]
 * @param assetBalance [Общий баланс портфеля]
 * @param date [Дата, когда был такой баланс]
 */
@Entity
data class AssetBalanceHistory( // баланс портфеля

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetBalance: Float, // стоимость портфеля
    @ColumnInfo(name = "date") val date: Date // поле с датой
)
