package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_records")
data class DonationRecordEntity(
    @PrimaryKey
    val id: String,
    val donorId: String,
    val dateMillis: Long,
    val bloodBankName: String,
    val component: String,
    val units: Int,
    val certificateIssued: Boolean = true
)
