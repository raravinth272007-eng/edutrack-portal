package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.DonationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationRecordDao {
    @Query("SELECT * FROM donation_records WHERE donorId = :donorId ORDER BY dateMillis DESC")
    fun getRecordsForDonor(donorId: String): Flow<List<DonationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DonationRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DonationRecordEntity>)

    @Query("SELECT COUNT(*) FROM donation_records")
    suspend fun count(): Int
}
