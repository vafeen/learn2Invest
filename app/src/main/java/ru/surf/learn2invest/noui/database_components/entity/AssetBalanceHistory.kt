package ru.surf.learn2invest.noui.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AssetBalanceHistory( // баланс портфеля

    @PrimaryKey(autoGenerate = true) val id: Int = 0, val assetBalance: Float // стоимость портфеля
)