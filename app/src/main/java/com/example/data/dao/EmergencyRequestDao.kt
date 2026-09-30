package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.EmergencyRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyRequestDao {
    @Query("SELECT * FROM emergency_requests ORDER BY createdAtMillis DESC")
    fun getAllRequests(): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE isFulfilled = 0 ORDER BY createdAtMillis DESC")
    fun getActiveRequests(): Flow<List<EmergencyRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: EmergencyRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<EmergencyRequestEntity>)

    @Update
    suspend fun update(request: EmergencyRequestEntity)

    @Query("UPDATE emergency_requests SET isFulfilled = :isFulfilled WHERE id = :id")
    suspend fun setFulfilled(id: String, isFulfilled: Boolean)

    @Query("DELETE FROM emergency_requests WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM emergency_requests")
    suspend fun count(): Int
}
