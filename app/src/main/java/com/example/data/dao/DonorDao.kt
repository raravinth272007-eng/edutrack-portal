package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.DonorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY distanceKm ASC")
    fun getAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE id = :id LIMIT 1")
    fun getDonorById(id: String): Flow<DonorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donors: List<DonorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donor: DonorEntity)

    @Update
    suspend fun update(donor: DonorEntity)

    @Query("UPDATE donors SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: String, isAvailable: Boolean)

    @Query("UPDATE donors SET hasConsentedContact = :consented WHERE id = :id")
    suspend fun updateConsent(id: String, consented: Boolean)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun count(): Int
}
