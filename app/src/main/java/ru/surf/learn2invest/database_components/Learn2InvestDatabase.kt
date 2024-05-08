package ru.surf.learn2invest.database_components

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.surf.learn2invest.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.database_components.dao.CoinDao
import ru.surf.learn2invest.database_components.dao.CoinReviewDao
import ru.surf.learn2invest.database_components.dao.PriceAlertDao
import ru.surf.learn2invest.database_components.dao.ProfileDao
import ru.surf.learn2invest.database_components.dao.TransactionCoinSpecificDao
import ru.surf.learn2invest.database_components.dao.TransactionDao
import ru.surf.learn2invest.database_components.entity.Coin
import ru.surf.learn2invest.database_components.entity.CoinReview
import ru.surf.learn2invest.database_components.entity.AssetInvest
import ru.surf.learn2invest.database_components.entity.PriceAlert
import ru.surf.learn2invest.database_components.entity.Profile
import ru.surf.learn2invest.database_components.entity.Transaction
import ru.surf.learn2invest.database_components.entity.TransactionCoinSpecific

@Database(
    entities = [
        AssetInvest::class,
        Coin::class,
        CoinReview::class,
        PriceAlert::class,
        Profile::class,
        Transaction::class,
        TransactionCoinSpecific::class,
    ], version = 1
)
abstract class Database : RoomDatabase() {
    companion object {
        const val NAME = "learn2investDatabase.db"
    }


    abstract fun assetInvestDao(): AssetInvestDao


    abstract fun coinDao(): CoinDao


    abstract fun coinReviewDao(): CoinReviewDao


    abstract fun priceAlertDao(): PriceAlertDao


    abstract fun profileDao(): ProfileDao


    abstract fun transactionDao(): TransactionDao


    abstract fun transactionCoinSpecificDao(): TransactionCoinSpecificDao


}