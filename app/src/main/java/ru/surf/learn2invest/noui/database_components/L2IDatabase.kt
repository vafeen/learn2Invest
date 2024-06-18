package ru.surf.learn2invest.noui.database_components

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.noui.database_components.dao.TransactionDao
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.database_components.entity.Transaction.Transaction

@Database(
    entities = [
        AssetBalanceHistory::class,
        AssetInvest::class,
        Profile::class,
        SearchedCoin::class,
        Transaction::class,
    ], version = 1
)

@TypeConverters(Converters::class)
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
    abstract fun profileDao(): ProfileDao
    abstract fun searchedCoinDao(): SearchedCoinDao
    abstract fun transactionDao(): TransactionDao
}