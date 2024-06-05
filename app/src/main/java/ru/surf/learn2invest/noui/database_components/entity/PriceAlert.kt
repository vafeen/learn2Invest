package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Объект задания уведомления об изменении цены
 */

@Entity
class PriceAlert(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val symbol: String, //Абревиатура
    val coinPrice: Float, //Целевая Цена
    val changePercent24Hr: Float, //Целевой Процент изменения
    val comment: String?
)
