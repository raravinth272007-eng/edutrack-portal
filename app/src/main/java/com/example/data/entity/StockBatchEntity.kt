package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_batches")
data class StockBatchEntity(
    @PrimaryKey
    val id: String,
    val bloodBankId: String,
    val bloodBankName: String,
    val bloodGroup: String,
    val component: String,
    val units: Int,
    val collectionDateMillis: Long,
    val expiryDateMillis: Long,
    val batchNumber: String,
    val isDispensed: Boolean = false
) {
    fun daysUntilExpiry(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val diff = expiryDateMillis - currentTimeMillis
        return (diff / (24L * 60L * 60L * 1000L)).toInt()
    }

    fun isExpired(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        return expiryDateMillis <= currentTimeMillis
    }

    fun isExpiringSoon(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        val days = daysUntilExpiry(currentTimeMillis)
        return days in 0..5
    }
}
