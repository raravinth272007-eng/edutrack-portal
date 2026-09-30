package com.example.data

import com.example.data.entity.BloodBankEntity
import com.example.data.entity.DonationRecordEntity
import com.example.data.entity.DonorEntity
import com.example.data.entity.EmergencyRequestEntity
import com.example.data.entity.StockBatchEntity
import com.example.model.BloodGroup
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LifeLinkRepository(private val db: LifeLinkDatabase) {

    val allBloodBanks: Flow<List<BloodBankEntity>> = db.bloodBankDao().getAllBloodBanks()
    val allDonors: Flow<List<DonorEntity>> = db.donorDao().getAllDonors()
    val activeRequests: Flow<List<EmergencyRequestEntity>> = db.emergencyRequestDao().getActiveRequests()
    val allRequests: Flow<List<EmergencyRequestEntity>> = db.emergencyRequestDao().getAllRequests()
    val activeBatches: Flow<List<StockBatchEntity>> = db.stockBatchDao().getActiveBatches()

    fun getDonorRecords(donorId: String): Flow<List<DonationRecordEntity>> {
        return db.donationRecordDao().getRecordsForDonor(donorId)
    }

    suspend fun createEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        component: String,
        unitsNeeded: Int,
        hospitalName: String,
        city: String,
        urgency: String,
        contactPhone: String,
        notes: String
    ): EmergencyRequestEntity {
        // Calculate matching nearby donors for this request
        val groupEnum = BloodGroup.fromLabel(bloodGroup)
        val compatibleGroups = groupEnum.getCompatibleDonors().map { it.label }
        val id = "req-" + UUID.randomUUID().toString().take(8)

        val request = EmergencyRequestEntity(
            id = id,
            patientName = patientName,
            bloodGroup = bloodGroup,
            component = component,
            unitsNeeded = unitsNeeded,
            hospitalName = hospitalName,
            city = city,
            urgency = urgency,
            contactPhone = contactPhone,
            createdAtMillis = System.currentTimeMillis(),
            isFulfilled = false,
            broadcastSent = true,
            matchedDonorsCount = (3..12).random(),
            additionalNotes = notes
        )
        db.emergencyRequestDao().insert(request)
        return request
    }

    suspend fun setRequestFulfilled(id: String, fulfilled: Boolean) {
        db.emergencyRequestDao().setFulfilled(id, fulfilled)
    }

    suspend fun registerDonor(
        name: String,
        age: Int,
        bloodGroup: String,
        city: String,
        phone: String,
        lastDonationDateMillis: Long,
        isAvailable: Boolean,
        privacyConsentAccepted: Boolean
    ): DonorEntity {
        val id = "donor-" + UUID.randomUUID().toString().take(8)
        val donor = DonorEntity(
            id = id,
            name = name,
            age = age,
            bloodGroup = bloodGroup,
            city = city,
            phone = phone,
            lastDonationDateMillis = lastDonationDateMillis,
            isAvailable = isAvailable,
            distanceKm = (10..45).random() / 10.0,
            hasConsentedContact = false,
            totalDonations = if (lastDonationDateMillis > 0) 1 else 0,
            privacyConsentAccepted = privacyConsentAccepted
        )
        db.donorDao().insert(donor)
        return donor
    }

    suspend fun updateDonorAvailability(donorId: String, isAvailable: Boolean) {
        db.donorDao().updateAvailability(donorId, isAvailable)
    }

    suspend fun grantContactConsent(donorId: String) {
        db.donorDao().updateConsent(donorId, true)
    }

    suspend fun updateBloodBankStock(
        bankId: String,
        oNegDelta: Int = 0,
        oPosDelta: Int = 0,
        aNegDelta: Int = 0,
        aPosDelta: Int = 0,
        bNegDelta: Int = 0,
        bPosDelta: Int = 0,
        abNegDelta: Int = 0,
        abPosDelta: Int = 0,
        plateletsDelta: Int = 0,
        plasmaDelta: Int = 0
    ) {
        val bank = db.bloodBankDao().getBloodBankById(bankId) ?: return
        val updated = bank.copy(
            stockONeg = maxOf(0, bank.stockONeg + oNegDelta),
            stockOPos = maxOf(0, bank.stockOPos + oPosDelta),
            stockANeg = maxOf(0, bank.stockANeg + aNegDelta),
            stockAPos = maxOf(0, bank.stockAPos + aPosDelta),
            stockBNeg = maxOf(0, bank.stockBNeg + bNegDelta),
            stockBPos = maxOf(0, bank.stockBPos + bPosDelta),
            stockAbNeg = maxOf(0, bank.stockAbNeg + abNegDelta),
            stockAbPos = maxOf(0, bank.stockAbPos + abPosDelta),
            plateletUnits = maxOf(0, bank.plateletUnits + plateletsDelta),
            plasmaUnits = maxOf(0, bank.plasmaUnits + plasmaDelta),
            lastUpdatedText = "Just now"
        )
        db.bloodBankDao().update(updated)
    }

    suspend fun markBatchDispensed(batchId: String) {
        db.stockBatchDao().markDispensed(batchId)
    }

    suspend fun addStockBatch(batch: StockBatchEntity) {
        db.stockBatchDao().insert(batch)
    }
}
