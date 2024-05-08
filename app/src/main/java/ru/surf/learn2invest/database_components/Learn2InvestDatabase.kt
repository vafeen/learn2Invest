package ru.surf.learn2invest.database_components

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
/**
 * Доступ к данным осуществляется в coroutineScope{} с помощью
 *
 *  [mainDB](ru.surf.learn2invest.main.App.mainDB)
 *
 * Посредством обращения через нее к определенным объектам DAO,
 *
 * например:
 * [assetInvestDao](Learn2InvestDatabase.assetInvestDao),
 *
 * а далее к одному из методов:
 * - [getAll](AssetInvestDao.getAll)
 * - [insertAll](AssetInvestDao.insertAll)
 * - [delete](AssetInvestDao.delete)
 *
 * Полный пример:
 *```
 * var someList: List<Something>
 *
 * lifecycleScope.launch(Dispatchers.Main) {
 *
 * Learn2InvestApp.mainDB.someDao().getAll().collect { someList = it }
 * }
 * ```
 */
abstract class Learn2InvestDatabase : RoomDatabase() {
    companion object {
        private const val NAME = "learn2investDatabase.db"

        fun buildDatabase(context: Context): Learn2InvestDatabase {
            return Room
                .databaseBuilder(
                    context = context,
                    klass = Learn2InvestDatabase::class.java,
                    name = NAME
                )
                .build()
        }
    }



    abstract fun assetInvestDao(): AssetInvestDao


    abstract fun coinDao(): CoinDao


    abstract fun coinReviewDao(): CoinReviewDao


    abstract fun priceAlertDao(): PriceAlertDao


    abstract fun profileDao(): ProfileDao


    abstract fun transactionDao(): TransactionDao


    abstract fun transactionCoinSpecificDao(): TransactionCoinSpecificDao


}