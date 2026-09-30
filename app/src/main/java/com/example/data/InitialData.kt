package com.example.data

import com.example.data.entity.BloodBankEntity
import com.example.data.entity.DonationRecordEntity
import com.example.data.entity.DonorEntity
import com.example.data.entity.EmergencyRequestEntity
import com.example.data.entity.StockBatchEntity
import com.example.model.UrgencyLevel

object InitialData {

    suspend fun populateDatabase(db: LifeLinkDatabase) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60L * 60L * 1000L

        // 1. Initial Blood Banks
        if (db.bloodBankDao().count() == 0) {
            val bloodBanks = listOf(
                BloodBankEntity(
                    id = "bb-1",
                    name = "Central Metro Red Cross Blood Center",
                    facilityType = "Regional Blood Transfusion Center",
                    address = "12 Medical Enclave, Ring Road",
                    city = "Central District",
                    phone = "+1 800-555-0199",
                    distanceKm = 1.4,
                    latitude = 28.6139,
                    longitude = 77.2090,
                    lastUpdatedText = "Updated 10m ago",
                    stockONeg = 2, // Low stock alert!
                    stockOPos = 24,
                    stockANeg = 1, // Critical!
                    stockAPos = 19,
                    stockBNeg = 4,
                    stockBPos = 22,
                    stockAbNeg = 1, // Critical!
                    stockAbPos = 8,
                    plasmaUnits = 42,
                    plateletUnits = 3, // Low!
                    rbcUnits = 81
                ),
                BloodBankEntity(
                    id = "bb-2",
                    name = "City Apex Trauma Center & Blood Bank",
                    facilityType = "Hospital Blood Bank (24x7 Emergency)",
                    address = "45 Healthcare Boulevard, Sector 4",
                    city = "Central District",
                    phone = "+1 800-555-0214",
                    distanceKm = 3.2,
                    latitude = 28.6250,
                    longitude = 77.2180,
                    lastUpdatedText = "Updated 25m ago",
                    stockONeg = 5,
                    stockOPos = 31,
                    stockANeg = 4,
                    stockAPos = 14,
                    stockBNeg = 6,
                    stockBPos = 18,
                    stockAbNeg = 3,
                    stockAbPos = 9,
                    plasmaUnits = 55,
                    plateletUnits = 12,
                    rbcUnits = 90
                ),
                BloodBankEntity(
                    id = "bb-3",
                    name = "St. Jude Memorial Hospital Blood Bank",
                    facilityType = "Charitable Hospital Blood Bank",
                    address = "88 Hope Avenue, North Wing",
                    city = "North Zone",
                    phone = "+1 800-555-0377",
                    distanceKm = 5.8,
                    latitude = 28.6400,
                    longitude = 77.2250,
                    lastUpdatedText = "Updated 1h ago",
                    stockONeg = 1, // Critical shortage!
                    stockOPos = 12,
                    stockANeg = 2,
                    stockAPos = 11,
                    stockBNeg = 3,
                    stockBPos = 16,
                    stockAbNeg = 2,
                    stockAbPos = 5,
                    plasmaUnits = 20,
                    plateletUnits = 4,
                    rbcUnits = 52
                ),
                BloodBankEntity(
                    id = "bb-4",
                    name = "Rotary LifeCare Voluntary Blood Bank",
                    facilityType = "Community Voluntary Bank",
                    address = "104 Civic Center Road, West Park",
                    city = "West Zone",
                    phone = "+1 800-555-0455",
                    distanceKm = 8.1,
                    latitude = 28.5900,
                    longitude = 77.1800,
                    lastUpdatedText = "Updated 2h ago",
                    stockONeg = 6,
                    stockOPos = 28,
                    stockANeg = 5,
                    stockAPos = 22,
                    stockBNeg = 7,
                    stockBPos = 25,
                    stockAbNeg = 4,
                    stockAbPos = 10,
                    plasmaUnits = 60,
                    plateletUnits = 14,
                    rbcUnits = 107
                ),
                BloodBankEntity(
                    id = "bb-5",
                    name = "Sunrise Multi-Specialty Blood Bank",
                    facilityType = "Private Super-Specialty Bank",
                    address = "210 Tech Corridor, South Gate",
                    city = "South Zone",
                    phone = "+1 800-555-0812",
                    distanceKm = 11.4,
                    latitude = 28.5600,
                    longitude = 77.2300,
                    lastUpdatedText = "Updated 3h ago",
                    stockONeg = 0, // Out of stock!
                    stockOPos = 15,
                    stockANeg = 2,
                    stockAPos = 8,
                    stockBNeg = 1,
                    stockBPos = 19,
                    stockAbNeg = 0,
                    stockAbPos = 6,
                    plasmaUnits = 18,
                    plateletUnits = 2, // Critical!
                    rbcUnits = 51
                )
            )
            db.bloodBankDao().insertAll(bloodBanks)
        }

        // 2. Initial Donors (Demonstrating eligible vs ineligible 90-day cooldown)
        if (db.donorDao().count() == 0) {
            val donors = listOf(
                // Logged-in demo user donor
                DonorEntity(
                    id = "donor-me",
                    name = "Alex Vance (You)",
                    age = 29,
                    bloodGroup = "O−",
                    city = "Central District",
                    phone = "+1 555-010-4492",
                    lastDonationDateMillis = now - (110L * oneDayMillis), // 110 days ago -> ELIGIBLE!
                    isAvailable = true,
                    distanceKm = 0.8,
                    latitude = 28.6145,
                    longitude = 77.2095,
                    hasConsentedContact = true,
                    totalDonations = 5,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-1",
                    name = "David Miller",
                    age = 34,
                    bloodGroup = "O−", // Universal donor
                    city = "Central District",
                    phone = "+1 555-019-3381",
                    lastDonationDateMillis = now - (98L * oneDayMillis), // >90 days ago -> ELIGIBLE
                    isAvailable = true,
                    distanceKm = 2.1,
                    latitude = 28.6180,
                    longitude = 77.2120,
                    hasConsentedContact = false,
                    totalDonations = 8,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-2",
                    name = "Sarah Jenkins",
                    age = 27,
                    bloodGroup = "A+",
                    city = "Central District",
                    phone = "+1 555-014-9923",
                    lastDonationDateMillis = now - (35L * oneDayMillis), // 35 days ago -> INELIGIBLE (55 days left)
                    isAvailable = true,
                    distanceKm = 3.4,
                    latitude = 28.6220,
                    longitude = 77.2050,
                    hasConsentedContact = false,
                    totalDonations = 3,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-3",
                    name = "Rahul Sharma",
                    age = 31,
                    bloodGroup = "B+",
                    city = "North Zone",
                    phone = "+1 555-012-7711",
                    lastDonationDateMillis = now - (140L * oneDayMillis), // ELIGIBLE
                    isAvailable = true,
                    distanceKm = 4.2,
                    latitude = 28.6310,
                    longitude = 77.2190,
                    hasConsentedContact = false,
                    totalDonations = 4,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-4",
                    name = "Elena Rostova",
                    age = 25,
                    bloodGroup = "O+",
                    city = "Central District",
                    phone = "+1 555-016-8844",
                    lastDonationDateMillis = 0L, // First time donor -> ELIGIBLE
                    isAvailable = true,
                    distanceKm = 2.9,
                    latitude = 28.6110,
                    longitude = 77.2140,
                    hasConsentedContact = true,
                    totalDonations = 0,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-5",
                    name = "Marcus Chen",
                    age = 38,
                    bloodGroup = "AB−", // Rare
                    city = "West Zone",
                    phone = "+1 555-018-6221",
                    lastDonationDateMillis = now - (120L * oneDayMillis), // ELIGIBLE
                    isAvailable = false, // Temporarily offline toggle test
                    distanceKm = 6.5,
                    latitude = 28.6010,
                    longitude = 77.1900,
                    hasConsentedContact = false,
                    totalDonations = 6,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-6",
                    name = "Priya Patel",
                    age = 29,
                    bloodGroup = "B−",
                    city = "Central District",
                    phone = "+1 555-017-4552",
                    lastDonationDateMillis = now - (72L * oneDayMillis), // Ineligible (18 days left)
                    isAvailable = true,
                    distanceKm = 3.9,
                    latitude = 28.6290,
                    longitude = 77.2080,
                    hasConsentedContact = false,
                    totalDonations = 2,
                    privacyConsentAccepted = true
                ),
                DonorEntity(
                    id = "donor-7",
                    name = "James Wilson",
                    age = 42,
                    bloodGroup = "AB+", // Universal recipient
                    city = "South Zone",
                    phone = "+1 555-011-3091",
                    lastDonationDateMillis = now - (105L * oneDayMillis), // ELIGIBLE
                    isAvailable = true,
                    distanceKm = 7.8,
                    latitude = 28.5800,
                    longitude = 77.2200,
                    hasConsentedContact = false,
                    totalDonations = 11,
                    privacyConsentAccepted = true
                )
            )
            db.donorDao().insertAll(donors)
        }

        // 3. Initial Active Emergency Requests
        if (db.emergencyRequestDao().count() == 0) {
            val requests = listOf(
                EmergencyRequestEntity(
                    id = "req-1",
                    patientName = "Emily Watson",
                    bloodGroup = "O−",
                    component = "Whole Blood",
                    unitsNeeded = 3,
                    hospitalName = "City Apex Trauma ICU, Ward 3",
                    city = "Central District",
                    urgency = UrgencyLevel.CRITICAL.name,
                    contactPhone = "+1 555-019-8800",
                    createdAtMillis = now - (35 * 60 * 1000L), // 35m ago
                    isFulfilled = false,
                    broadcastSent = true,
                    matchedDonorsCount = 6,
                    additionalNotes = "Emergency vehicular accident, multiple fractures, immediate surgery."
                ),
                EmergencyRequestEntity(
                    id = "req-2",
                    patientName = "Karan Johar",
                    bloodGroup = "B+",
                    component = "Platelets (SDP / RDP)",
                    unitsNeeded = 2,
                    hospitalName = "St. Jude Oncology Center",
                    city = "North Zone",
                    urgency = UrgencyLevel.WITHIN_24H.name,
                    contactPhone = "+1 555-014-4411",
                    createdAtMillis = now - (3 * 3600 * 1000L), // 3h ago
                    isFulfilled = false,
                    broadcastSent = true,
                    matchedDonorsCount = 14,
                    additionalNotes = "Chemotherapy recovery, platelet count dropped below 20,000."
                ),
                EmergencyRequestEntity(
                    id = "req-3",
                    patientName = "Grace Kelly",
                    bloodGroup = "A−",
                    component = "Packed Red Blood Cells (PRBC)",
                    unitsNeeded = 2,
                    hospitalName = "Central Metro Maternity Wing",
                    city = "Central District",
                    urgency = UrgencyLevel.PLANNED.name,
                    contactPhone = "+1 555-017-9912",
                    createdAtMillis = now - (18 * 3600 * 1000L),
                    isFulfilled = true, // Demo fulfilled request
                    broadcastSent = true,
                    matchedDonorsCount = 8,
                    additionalNotes = "Scheduled Caesarean section - units secured successfully."
                )
            )
            db.emergencyRequestDao().insertAll(requests)
        }

        // 4. Initial Donation History for Demo User
        if (db.donationRecordDao().count() == 0) {
            val records = listOf(
                DonationRecordEntity(
                    id = "rec-1",
                    donorId = "donor-me",
                    dateMillis = now - (110L * oneDayMillis),
                    bloodBankName = "Central Metro Red Cross Blood Center",
                    component = "Whole Blood",
                    units = 1,
                    certificateIssued = true
                ),
                DonationRecordEntity(
                    id = "rec-2",
                    donorId = "donor-me",
                    dateMillis = now - (220L * oneDayMillis),
                    bloodBankName = "City Apex Trauma Center",
                    component = "Whole Blood",
                    units = 1,
                    certificateIssued = true
                ),
                DonationRecordEntity(
                    id = "rec-3",
                    donorId = "donor-me",
                    dateMillis = now - (330L * oneDayMillis),
                    bloodBankName = "Rotary LifeCare Voluntary Blood Bank",
                    component = "Plasma",
                    units = 2,
                    certificateIssued = true
                )
            )
            db.donationRecordDao().insertAll(records)
        }

        // 5. Initial Batches for Stock Expiry Tracking & Low Stock Alerts
        if (db.stockBatchDao().count() == 0) {
            val batches = listOf(
                StockBatchEntity(
                    id = "batch-101",
                    bloodBankId = "bb-1",
                    bloodBankName = "Central Metro Red Cross",
                    bloodGroup = "O−",
                    component = "Platelets (SDP)",
                    units = 2,
                    collectionDateMillis = now - (3L * oneDayMillis),
                    expiryDateMillis = now + (2L * oneDayMillis), // Expiring in 2 days!
                    batchNumber = "PLT-2026-904"
                ),
                StockBatchEntity(
                    id = "batch-102",
                    bloodBankId = "bb-1",
                    bloodBankName = "Central Metro Red Cross",
                    bloodGroup = "A+",
                    component = "Whole Blood",
                    units = 4,
                    collectionDateMillis = now - (31L * oneDayMillis),
                    expiryDateMillis = now + (4L * oneDayMillis), // Expiring in 4 days!
                    batchNumber = "WB-2026-441"
                ),
                StockBatchEntity(
                    id = "batch-103",
                    bloodBankId = "bb-2",
                    bloodBankName = "City Apex Trauma Center",
                    bloodGroup = "B+",
                    component = "Packed Red Blood Cells (PRBC)",
                    units = 6,
                    collectionDateMillis = now - (20L * oneDayMillis),
                    expiryDateMillis = now + (22L * oneDayMillis),
                    batchNumber = "PRBC-2026-788"
                ),
                StockBatchEntity(
                    id = "batch-104",
                    bloodBankId = "bb-1",
                    bloodBankName = "Central Metro Red Cross",
                    bloodGroup = "O+",
                    component = "Fresh Frozen Plasma (FFP)",
                    units = 15,
                    collectionDateMillis = now - (60L * oneDayMillis),
                    expiryDateMillis = now + (305L * oneDayMillis),
                    batchNumber = "FFP-2026-112"
                )
            )
            db.stockBatchDao().insertAll(batches)
        }
    }
}
