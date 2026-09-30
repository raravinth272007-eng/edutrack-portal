package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BloodGroup

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val age: Int,
    val bloodGroup: String,
    val city: String,
    val phone: String,
    val lastDonationDateMillis: Long, // 0L if never donated
    val isAvailable: Boolean = true,
    val distanceKm: Double = 3.2,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val hasConsentedContact: Boolean = false,
    val totalDonations: Int = 1,
    val privacyConsentAccepted: Boolean = true
) {
    companion object {
        const val COOLDOWN_DAYS = 90
        const val COOLDOWN_MILLIS = COOLDOWN_DAYS * 24L * 60L * 60L * 1000L
    }

    fun isEligible(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        if (lastDonationDateMillis <= 0L) return true
        val diff = currentTimeMillis - lastDonationDateMillis
        return diff >= COOLDOWN_MILLIS
    }

    fun daysUntilEligible(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        if (lastDonationDateMillis <= 0L) return 0
        val diff = currentTimeMillis - lastDonationDateMillis
        val daysPassed = (diff / (24L * 60L * 60L * 1000L)).toInt()
        val remaining = COOLDOWN_DAYS - daysPassed
        return if (remaining > 0) remaining else 0
    }

    fun getBloodGroupEnum(): BloodGroup {
        return BloodGroup.fromLabel(bloodGroup)
    }

    fun maskedPhone(): String {
        if (phone.length <= 4) return phone
        val last4 = phone.takeLast(4)
        val prefix = if (phone.startsWith("+")) phone.take(3) else phone.take(2)
        return "$prefix •••• ••$last4"
    }
}
