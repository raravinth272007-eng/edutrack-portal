package com.example.ui

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी")
}

data class TranslationStrings(
    val appTitle: String,
    val appSubtitle: String,
    val sosButton: String,
    val sosSubtitle: String,
    val quickSearch: String,
    val allBloodGroups: String,
    val findDonorsTab: String,
    val bloodBanksTab: String,
    val mapTab: String,
    val donorHubTab: String,
    val adminTab: String,
    val learnTab: String,
    val urgentRequestsTitle: String,
    val broadcastButton: String,
    val unitsNeeded: String,
    val callBank: String,
    val getDirections: String,
    val requestConsent: String,
    val consentApproved: String,
    val whatsappContact: String,
    val callDonor: String,
    val eligibleStatus: String,
    val ineligibleStatus: String,
    val daysRemaining: String,
    val compatibilityNotice: String,
    val donorRegistrationTitle: String,
    val submitRegistration: String,
    val lowStockWarning: String,
    val expiryAlert: String,
    val markFulfilled: String,
    val fulfilledBadge: String,
    val openBadge: String
)

object LocalizationManager {
    val english = TranslationStrings(
        appTitle = "LifeLink",
        appSubtitle = "Blood Availability & Emergency Donor Finder",
        sosButton = "SOS EMERGENCY REQUEST",
        sosSubtitle = "Tap to broadcast urgent blood requirement to nearby donors & banks",
        quickSearch = "Quick Blood Availability Search",
        allBloodGroups = "All Groups",
        findDonorsTab = "Find Donors",
        bloodBanksTab = "Blood Banks",
        mapTab = "Live Map",
        donorHubTab = "Donor Hub",
        adminTab = "Admin Stock",
        learnTab = "Eligibility & FAQs",
        urgentRequestsTitle = "Active Emergency Requests",
        broadcastButton = "Broadcast Alert",
        unitsNeeded = "units required",
        callBank = "Call Bank",
        getDirections = "Directions",
        requestConsent = "Request Phone Access",
        consentApproved = "Consent Granted",
        whatsappContact = "WhatsApp",
        callDonor = "Call Donor",
        eligibleStatus = "Eligible to Donate",
        ineligibleStatus = "Ineligible (90-day cooldown)",
        daysRemaining = "days remaining",
        compatibilityNotice = "Universal O- can donate to all. AB+ can receive from all.",
        donorRegistrationTitle = "Register as a Blood Donor",
        submitRegistration = "Join as Volunteer Donor",
        lowStockWarning = "Low Stock Alert (< 3 units)",
        expiryAlert = "Batch Expiring Soon",
        markFulfilled = "Mark Fulfilled",
        fulfilledBadge = "FULFILLED",
        openBadge = "URGENT OPEN"
    )

    val hindi = TranslationStrings(
        appTitle = "लाइफ-लिंक",
        appSubtitle = "रक्त उपलब्धता एवं आपातकालीन रक्तदाता खोज",
        sosButton = "आपातकालीन SOS अनुरोध",
        sosSubtitle = "आसपास के रक्तदाताओं और ब्लड बैंकों को तुरंत अलर्ट भेजें",
        quickSearch = "रक्त उपलब्धता त्वरित खोज",
        allBloodGroups = "सभी रक्त समूह",
        findDonorsTab = "रक्तदाता खोजें",
        bloodBanksTab = "ब्लड बैंक",
        mapTab = "लाइव मानचित्र",
        donorHubTab = "डोनर हब",
        adminTab = "स्टॉक प्रबंधन",
        learnTab = "योग्यता एवं नियम",
        urgentRequestsTitle = "सक्रिय आपातकालीन अनुरोध",
        broadcastButton = "अलर्ट प्रसारित करें",
        unitsNeeded = "यूनिट आवश्यक",
        callBank = "कॉल करें",
        getDirections = "दिशा-निर्देश",
        requestConsent = "संपर्क सहमति मांगें",
        consentApproved = "सहमति प्राप्त",
        whatsappContact = "व्हाट्सएप",
        callDonor = "रक्तदाता को कॉल",
        eligibleStatus = "रक्तदान हेतु योग्य",
        ineligibleStatus = "अयोग्य (90 दिन विश्राम अवधि)",
        daysRemaining = "दिन शेष",
        compatibilityNotice = "O- सभी को रक्त दे सकता है। AB+ सभी से रक्त ले सकता है।",
        donorRegistrationTitle = "रक्तदाता के रूप में पंजीकरण",
        submitRegistration = "स्वयंसेवक रक्तदाता बनें",
        lowStockWarning = "कम स्टॉक चेतावनी (< 3 यूनिट)",
        expiryAlert = "यूनिट शीघ्र समाप्त होने वाली है",
        markFulfilled = "पूर्ण चिह्नित करें",
        fulfilledBadge = "पूर्ण हुआ",
        openBadge = "अति आवश्यक"
    )

    fun get(lang: AppLanguage): TranslationStrings {
        return when (lang) {
            AppLanguage.ENGLISH -> english
            AppLanguage.HINDI -> hindi
        }
    }
}
