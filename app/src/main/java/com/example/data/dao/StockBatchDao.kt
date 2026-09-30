package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.StockBatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockBatchDao {
    @Query("SELECT * FROM stock_batches WHERE isDispensed = 0 ORDER BY expiryDateMillis ASC")
    fun getActiveBatches(): Flow<List<StockBatchEntity>>

    @Query("SELECT * FROM stock_batches WHERE bloodBankId = :bankId AND isDispensed = 0 ORDER BY expiryDateMillis ASC")
    fun getBatchesForBank(bankId: String): Flow<List<StockBatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: StockBatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(batches: List<StockBatchEntity>)

    @Update
    suspend fun update(batch: StockBatchEntity)

    @Query("UPDATE stock_batches SET isDispensed = 1 WHERE id = :id")
    suspend fun markDispensed(id: String)

    @Query("SELECT COUNT(*) FROM stock_batches")
    suspend fun count(): Int
}
