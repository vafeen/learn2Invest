package ru.surf.learn2invest.noui.database_components

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.PriceAlertDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.noui.database_components.dao.TransactionDao
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.database_components.entity.Transaction

@Database(
    entities = [
        AssetBalanceHistory::class,
        AssetInvest::class,
        PriceAlert::class,
        Profile::class,
        SearchedCoin::class,
        Transaction::class,
    ], version = 1
)
/**
 * Доступ к данным осуществляется в coroutineScope{} с помощью
 *
 *  [mainDB](ru.surf.learn2invest.ui.main.App.mainDB)
 *
 * Посредством обращения через нее к определенным объектам DAO,
 *
 * например:
 * [assetInvestDao](Learn2InvestDatabase.assetInvestDao),
 *
 * а далее к одному из методов:
 * - [getAllAsFlow](ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao.getAllAsFlow)
 * - [insertAll](ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao.insertAll)
 * - [delete](ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao.delete)
 *
 * Полный пример:
 *```
 * var someList: List<Something>
 *
 * lifecycleScope.launch(Dispatchers.Main) {
 *
 * Learn2InvestApp.mainDB.someDao().getAllAsFlow().collect { someList = it } // подписка на изменения
 *
 * someList = Learn2InvestApp.mainDB.someDao().getAllAsFlow().first() // разовая акция
 * }
 * ```
 *
 * и дальше можно просто делать свои манипуляции с объектами базы данных:
 * - [insertAll](AssetInvestDao.insertAll)
 * - [delete](AssetInvestDao.delete)
 */
abstract class L2IDatabase : RoomDatabase() {
    companion object {
        private const val NAME = "learn2investDatabase.db"


        /**
         * создание объекта базы данных
         */
        fun buildDatabase(context: Context): L2IDatabase {
            return Room.databaseBuilder(
                    context = context, klass = L2IDatabase::class.java, name = NAME
                ).build()
        }
    }


    abstract fun assetBalanceHistoryDao(): AssetBalanceHistoryDao

    abstract fun assetInvestDao(): AssetInvestDao

    abstract fun priceAlertDao(): PriceAlertDao

    abstract fun profileDao(): ProfileDao

    abstract fun searchedCoinDao(): SearchedCoinDao

    abstract fun transactionDao(): TransactionDao


}