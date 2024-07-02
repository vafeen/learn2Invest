package ru.surf.learn2invest.noui.database_components

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
import javax.inject.Singleton


/**
 * Репозиториq локальной базы данных для осуществления операций манипуляции с сущностями
 */
@Singleton
class DatabaseRepository @Inject constructor(
    private val db: L2IDatabase,
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao,
    private val assetInvestDao: AssetInvestDao,
    private val profileDao: ProfileDao,
    private val searchedCoinDao: SearchedCoinDao,
    private val transactionDao: TransactionDao,
) {
    private var idOfProfile = 0
    lateinit var profile: Profile

    fun enableProfileFlow(lifecycleCoroutineScope: LifecycleCoroutineScope) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            getAllAsFlowProfile().collect { profList ->
                if (profList.isNotEmpty()) {
                    profile = profList[idOfProfile]
                } else {
                    profile = Profile(
                        id = 0,
                        firstName = "undefined",
                        lastName = "undefined",
                        biometry = false,
                        fiatBalance = 0f,
                        assetBalance = 0f
                    )
                    insertAllProfile(profile)
                }
            }
        }
    }

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