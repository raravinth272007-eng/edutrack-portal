package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_banks")
data class BloodBankEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val facilityType: String, // e.g. "Govt District Hospital", "Red Cross Regional Center"
    val address: String,
    val city: String,
    val phone: String,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val lastUpdatedText: String,
    // Stock counts for each blood group for Whole Blood / RBC
    val stockONeg: Int = 4,
    val stockOPos: Int = 18,
    val stockANeg: Int = 3,
    val stockAPos: Int = 12,
    val stockBNeg: Int = 2,
    val stockBPos: Int = 15,
    val stockAbNeg: Int = 1,
    val stockAbPos: Int = 7,
    // Component stocks
    val plasmaUnits: Int = 24,
    val plateletUnits: Int = 6,
    val rbcUnits: Int = 45
) {
    fun getStockForGroup(bloodGroupLabel: String): Int {
        val normalized = bloodGroupLabel.replace("−", "-").uppercase().trim()
        return when (normalized) {
            "O-", "O−" -> stockONeg
            "O+" -> stockOPos
            "A-", "A−" -> stockANeg
            "A+" -> stockAPos
            "B-", "B−" -> stockBNeg
            "B+" -> stockBPos
            "AB-", "AB−" -> stockAbNeg
            "AB+" -> stockAbPos
            else -> stockOPos
        }
    }

    fun getTotalUnits(): Int {
        return stockONeg + stockOPos + stockANeg + stockAPos + stockBNeg + stockBPos + stockAbNeg + stockAbPos
    }

    fun hasLowStock(): Boolean {
        return stockONeg < 3 || stockANeg < 3 || stockBNeg < 3 || stockAbNeg < 2 || plateletUnits < 4
    }
}
