package ru.surf.learn2invest.noui.database_components

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.PriceAlertDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.dao.TransactionDao
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.PriceAlert
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.database_components.entity.Transaction

@Database(
    entities = [
        AssetInvest::class,
        PriceAlert::class,
        Profile::class,
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
 *
 * и дальше можно просто делать свои манипуляции с объектами базы данных:
 * - [insertAll](AssetInvestDao.insertAll)
 * - [delete](AssetInvestDao.delete)
 */
abstract class Learn2InvestDatabase : RoomDatabase() {
    companion object {
        private const val NAME = "learn2investDatabase.db"


        /**
         * создание объекта базы данных
         */
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


    abstract fun priceAlertDao(): PriceAlertDao


    abstract fun profileDao(): ProfileDao


    abstract fun transactionDao(): TransactionDao


}