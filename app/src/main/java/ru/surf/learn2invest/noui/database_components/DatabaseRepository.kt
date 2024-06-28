package ru.surf.learn2invest.noui.database_components

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.noui.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.noui.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.noui.database_components.dao.ProfileDao
import ru.surf.learn2invest.noui.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.noui.database_components.dao.TransactionDao
import ru.surf.learn2invest.noui.database_components.entity.AssetBalanceHistory
import ru.surf.learn2invest.noui.database_components.entity.AssetInvest
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.database_components.entity.SearchedCoin
import ru.surf.learn2invest.noui.database_components.entity.transaction.Transaction
import javax.inject.Inject


/**
 * Репозиториq локальной базы данных для осуществления операций манипуляции с сущностями
 */


class DatabaseRepository @Inject constructor(
    private var db: L2IDatabase,
    private var assetBalanceHistoryDao: AssetBalanceHistoryDao,
    private var assetInvestDao: AssetInvestDao,
    private var profileDao: ProfileDao,
    private var searchedCoinDao: SearchedCoinDao,
    private var transactionDao: TransactionDao,
) {
    var idOfProfile = 0
        private set
    lateinit var profile: Profile


    // assetBalanceHistory
    fun getAllAssetBalanceHistory(): Flow<List<AssetBalanceHistory>> =
        assetBalanceHistoryDao.getAllAsFlow()

    suspend fun insertAllAssetBalanceHistory(vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.insertAll(entities = entities)

    suspend fun insertByLimitAssetBalanceHistory(limit: Int, vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.insertByLimit(limit = limit, entities = entities)

    suspend fun updateAssetBalanceHistory(vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.update(entities = entities)

    suspend fun deleteAssetBalanceHistory(entity: AssetBalanceHistory) =
        assetBalanceHistoryDao.delete(entity)

    // AssetInvest
    fun getAllAsFlowAssetInvest(): Flow<List<AssetInvest>> =
        assetInvestDao.getAllAsFlow()

    suspend fun insertAllAssetInvest(vararg entities: AssetInvest) =
        assetInvestDao.insertAll(entities = entities)

    suspend fun updateAssetInvest(vararg entities: AssetInvest) =
        assetInvestDao.update(entities = entities)

    suspend fun deleteAssetInvest(entity: AssetInvest) =
        assetInvestDao.delete(entity)

    suspend fun getBySymbolAssetInvest(symbol: String) =
        assetInvestDao.getBySymbol(symbol)

    // profile
    fun getAllAsFlowProfile(): Flow<List<Profile>> =
        profileDao.getAllAsFlow()

    suspend fun insertAllProfile(vararg entities: Profile) =
        profileDao.insertAll(entities = entities)

    suspend fun updateProfile(vararg entities: Profile) =
        profileDao.update(entities = entities)

    suspend fun deleteProfile(entity: Profile) =
        profileDao.delete(entity)

    // searchedCoin
    fun getAllAsFlowSearchedCoin(): Flow<List<SearchedCoin>> =
        searchedCoinDao.getAllAsFlow()

    suspend fun insertAllSearchedCoin(vararg entities: SearchedCoin) =
        searchedCoinDao.insertAll(entities = entities)

    suspend fun updateSearchedCoin(vararg entities: SearchedCoin) =
        searchedCoinDao.update(entities = entities)

    suspend fun deleteSearchedCoin(entity: SearchedCoin) =
        searchedCoinDao.delete(entity)

    suspend fun deleteAllSearchedCoin() = searchedCoinDao.clearTable()

    suspend fun insertByLimitSearchedCoin(limit: Int, vararg entities: SearchedCoin) =
        searchedCoinDao.insertByLimit(limit = limit, entities = entities)

    // transaction
    fun getAllAsFlowTransaction(): Flow<List<Transaction>> =
        transactionDao.getAllAsFlow()

    suspend fun insertAllTransaction(vararg entities: Transaction) =
        transactionDao.insertAll(entities = entities)

    suspend fun updateTransaction(vararg entities: Transaction) =
        transactionDao.update(entities = entities)

    suspend fun deleteTransaction(entity: Transaction) =
        transactionDao.delete(entity)

    fun getFilteredBySymbolTransaction(filterSymbol: String): Flow<List<Transaction>> =
        transactionDao.getFilteredBySymbol(filterSymbol = filterSymbol)

    // ALL
    fun clearAllTables() = db.clearAllTables()
}