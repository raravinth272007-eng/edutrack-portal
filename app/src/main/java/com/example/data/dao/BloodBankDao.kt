package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.BloodBankEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodBankDao {
    @Query("SELECT * FROM blood_banks ORDER BY distanceKm ASC")
    fun getAllBloodBanks(): Flow<List<BloodBankEntity>>

    @Query("SELECT * FROM blood_banks WHERE id = :id LIMIT 1")
    suspend fun getBloodBankById(id: String): BloodBankEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bloodBanks: List<BloodBankEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bloodBank: BloodBankEntity)

    @Update
    suspend fun update(bloodBank: BloodBankEntity)

    @Query("SELECT COUNT(*) FROM blood_banks")
    suspend fun count(): Int
}
