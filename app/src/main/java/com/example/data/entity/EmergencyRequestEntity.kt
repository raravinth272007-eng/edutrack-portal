package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BloodGroup
import com.example.model.UrgencyLevel

@Entity(tableName = "emergency_requests")
data class EmergencyRequestEntity(
    @PrimaryKey
    val id: String,
    val patientName: String,
    val bloodGroup: String,
    val component: String = "Whole Blood",
    val unitsNeeded: Int,
    val hospitalName: String,
    val city: String,
    val urgency: String = UrgencyLevel.CRITICAL.name,
    val contactPhone: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isFulfilled: Boolean = false,
    val broadcastSent: Boolean = true,
    val matchedDonorsCount: Int = 0,
    val additionalNotes: String = ""
) {
    fun getBloodGroupEnum(): BloodGroup {
        return BloodGroup.fromLabel(bloodGroup)
    }

    fun getUrgencyEnum(): UrgencyLevel {
        return try {
            UrgencyLevel.valueOf(urgency)
        } catch (_: Exception) {
            UrgencyLevel.CRITICAL
        }
    }
}
