package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.InitialData
import com.example.data.LifeLinkDatabase
import com.example.data.LifeLinkRepository
import com.example.data.entity.BloodBankEntity
import com.example.data.entity.DonationRecordEntity
import com.example.data.entity.DonorEntity
import com.example.data.entity.EmergencyRequestEntity
import com.example.data.entity.StockBatchEntity
import com.example.model.BloodGroup
import com.example.model.UrgencyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val iconName: String) {
    HOME("Home"),
    BLOOD_BANKS("Banks"),
    FIND_DONORS("Donors"),
    MAP("Map"),
    DONOR_HUB("My Profile"),
    ADMIN("Admin"),
    LEARN("Info")
}

data class EmergencyFormState(
    val patientName: String = "",
    val bloodGroup: String = "O−",
    val component: String = "Whole Blood",
    val unitsNeeded: Int = 2,
    val hospitalName: String = "",
    val city: String = "Central District",
    val urgency: UrgencyLevel = UrgencyLevel.CRITICAL,
    val contactPhone: String = "",
    val notes: String = ""
)

data class DonorRegistrationFormState(
    val name: String = "",
    val age: String = "26",
    val bloodGroup: String = "O+",
    val city: String = "Central District",
    val phone: String = "",
    val lastDonationDaysAgo: String = "120", // or 0 for first time
    val isAvailable: Boolean = true,
    val consentAccepted: Boolean = true
)

class LifeLinkViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LifeLinkDatabase.getDatabase(application, viewModelScope)
    private val repository = LifeLinkRepository(db)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Language
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Emergency Request Form & Dialog State
    private val _showEmergencyModal = MutableStateFlow(false)
    val showEmergencyModal: StateFlow<Boolean> = _showEmergencyModal.asStateFlow()

    private val _emergencyForm = MutableStateFlow(EmergencyFormState())
    val emergencyForm: StateFlow<EmergencyFormState> = _emergencyForm.asStateFlow()

    private val _broadcastSuccess = MutableStateFlow<EmergencyRequestEntity?>(null)
    val broadcastSuccess: StateFlow<EmergencyRequestEntity?> = _broadcastSuccess.asStateFlow()

    // Blood Bank Search Filters
    val selectedBankBloodGroup = MutableStateFlow<String?>("O−")
    val selectedBankCity = MutableStateFlow("All Cities")
    val selectedBankComponent = MutableStateFlow("All")
    val bankSearchQuery = MutableStateFlow("")

    // Donor Finder Filters
    val donorTargetBloodGroup = MutableStateFlow<BloodGroup>(BloodGroup.O_NEG)
    val donorIncludeCompatible = MutableStateFlow(true)
    val donorMaxDistanceKm = MutableStateFlow(30f)
    val donorOnlyAvailable = MutableStateFlow(true)
    val donorOnlyEligible = MutableStateFlow(false)

    // Donor Hub / Registration
    val registrationForm = MutableStateFlow(DonorRegistrationFormState())
    val currentDonorId = MutableStateFlow("donor-me")
    val registrationSuccessMessage = MutableStateFlow<String?>(null)

    // Admin Panel
    val selectedAdminBankId = MutableStateFlow("bb-1")

    // Contact Consent Tracker (Donors for whom user has requested contact or received consent)
    private val _consentedDonorIds = MutableStateFlow<Set<String>>(setOf("donor-me", "donor-4"))
    val consentedDonorIds: StateFlow<Set<String>> = _consentedDonorIds.asStateFlow()

    // Data streams from repository
    val allBloodBanks: StateFlow<List<BloodBankEntity>> = repository.allBloodBanks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDonors: StateFlow<List<DonorEntity>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRequests: StateFlow<List<EmergencyRequestEntity>> = repository.activeRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<EmergencyRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBatches: StateFlow<List<StockBatchEntity>> = repository.activeBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val donorHistory: StateFlow<List<DonationRecordEntity>> = currentDonorId
        .combine(repository.allDonors) { id, _ -> id }
        .combine(repository.getDonorRecords("donor-me")) { _, records -> records }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Ensure database population
        viewModelScope.launch {
            InitialData.populateDatabase(db)
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun openEmergencyModal(prefilledGroup: String? = null) {
        if (prefilledGroup != null) {
            _emergencyForm.value = _emergencyForm.value.copy(bloodGroup = prefilledGroup)
        }
        _showEmergencyModal.value = true
    }

    fun closeEmergencyModal() {
        _showEmergencyModal.value = false
    }

    fun updateEmergencyForm(transform: (EmergencyFormState) -> EmergencyFormState) {
        _emergencyForm.value = transform(_emergencyForm.value)
    }

    fun submitEmergencyBroadcast() {
        val form = _emergencyForm.value
        if (form.patientName.isBlank() || form.hospitalName.isBlank() || form.contactPhone.isBlank()) {
            return
        }
        viewModelScope.launch {
            val req = repository.createEmergencyRequest(
                patientName = form.patientName,
                bloodGroup = form.bloodGroup,
                component = form.component,
                unitsNeeded = form.unitsNeeded,
                hospitalName = form.hospitalName,
                city = form.city,
                urgency = form.urgency.name,
                contactPhone = form.contactPhone,
                notes = form.notes
            )
            _showEmergencyModal.value = false
            _broadcastSuccess.value = req
            // Reset form
            _emergencyForm.value = EmergencyFormState()
        }
    }

    fun dismissBroadcastSuccess() {
        _broadcastSuccess.value = null
    }

    fun toggleRequestFulfilled(request: EmergencyRequestEntity) {
        viewModelScope.launch {
            repository.setRequestFulfilled(request.id, !request.isFulfilled)
        }
    }

    fun requestDonorContact(donorId: String) {
        viewModelScope.launch {
            _consentedDonorIds.value = _consentedDonorIds.value + donorId
            repository.grantContactConsent(donorId)
        }
    }

    fun toggleMyAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateDonorAvailability(currentDonorId.value, isAvailable)
        }
    }

    fun registerDonor(form: DonorRegistrationFormState) {
        if (form.name.isBlank() || form.phone.isBlank()) return
        val daysAgo = form.lastDonationDaysAgo.toLongOrNull() ?: 0L
        val lastDonationMillis = if (daysAgo > 0) {
            System.currentTimeMillis() - (daysAgo * 24L * 60L * 60L * 1000L)
        } else 0L

        val ageInt = form.age.toIntOrNull() ?: 25

        viewModelScope.launch {
            val newDonor = repository.registerDonor(
                name = form.name,
                age = ageInt,
                bloodGroup = form.bloodGroup,
                city = form.city,
                phone = form.phone,
                lastDonationDateMillis = lastDonationMillis,
                isAvailable = form.isAvailable,
                privacyConsentAccepted = form.consentAccepted
            )
            currentDonorId.value = newDonor.id
            registrationSuccessMessage.value = "Registration successful! Welcome to the LifeLink network."
        }
    }

    fun clearRegistrationMessage() {
        registrationSuccessMessage.value = null
    }

    fun adjustStock(bankId: String, bloodGroup: String, delta: Int) {
        viewModelScope.launch {
            when (bloodGroup) {
                "O−", "O-" -> repository.updateBloodBankStock(bankId, oNegDelta = delta)
                "O+" -> repository.updateBloodBankStock(bankId, oPosDelta = delta)
                "A−", "A-" -> repository.updateBloodBankStock(bankId, aNegDelta = delta)
                "A+" -> repository.updateBloodBankStock(bankId, aPosDelta = delta)
                "B−", "B-" -> repository.updateBloodBankStock(bankId, bNegDelta = delta)
                "B+" -> repository.updateBloodBankStock(bankId, bPosDelta = delta)
                "AB−", "AB-" -> repository.updateBloodBankStock(bankId, abNegDelta = delta)
                "AB+" -> repository.updateBloodBankStock(bankId, abPosDelta = delta)
                "Platelets" -> repository.updateBloodBankStock(bankId, plateletsDelta = delta)
                "Plasma" -> repository.updateBloodBankStock(bankId, plasmaDelta = delta)
            }
        }
    }

    fun dispenseBatch(batchId: String) {
        viewModelScope.launch {
            repository.markBatchDispensed(batchId)
        }
    }
}
