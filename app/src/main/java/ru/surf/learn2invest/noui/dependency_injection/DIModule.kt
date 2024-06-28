package ru.surf.learn2invest.noui.dependency_injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.L2IDatabase
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.noui.database_components.dao.TransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DIModule {
    private val NAME = "learn2investDatabase.db"

    @Provides
    @Singleton
    fun injectDatabase(@ApplicationContext context: Context): L2IDatabase {
        return Room.databaseBuilder(
            context = context, klass = L2IDatabase::class.java, name = NAME
        ).build()
    }

    @Provides
    @Singleton
    fun assetBalanceHistoryDao(db: L2IDatabase): AssetBalanceHistoryDao =
        db.assetBalanceHistoryDao()

    @Provides
    @Singleton
    fun assetInvestDao(db: L2IDatabase): AssetInvestDao = db.assetInvestDao()

    @Provides
    @Singleton
    fun profileDao(db: L2IDatabase): ProfileDao = db.profileDao()

    @Provides
    @Singleton
    fun searchedCoinDao(db: L2IDatabase): SearchedCoinDao = db.searchedCoinDao()

    @Provides
    @Singleton
    fun transactionDao(db: L2IDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun databaseRepository(
        db: L2IDatabase,
        assetBalanceHistoryDao: AssetBalanceHistoryDao,
        assetInvestDao: AssetInvestDao,
        profileDao: ProfileDao,
        searchedCoinDao: SearchedCoinDao,
        transactionDao: TransactionDao,
    ): DatabaseRepository = DatabaseRepository(
        db = db, assetBalanceHistoryDao = assetBalanceHistoryDao,
        assetInvestDao = assetInvestDao,
        profileDao = profileDao, searchedCoinDao = searchedCoinDao, transactionDao = transactionDao
    )
}