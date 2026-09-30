package com.example.model

enum class BloodGroup(val label: String) {
    O_NEG("O−"),
    O_POS("O+"),
    A_NEG("A−"),
    A_POS("A+"),
    B_NEG("B−"),
    B_POS("B+"),
    AB_NEG("AB−"),
    AB_POS("AB+");

    /**
     * Compatibility for red blood cells / whole blood.
     * O- is universal donor; AB+ is universal recipient.
     */
    fun canDonateRbcTo(recipient: BloodGroup): Boolean {
        return when (this) {
            O_NEG -> true // Universal RBC donor
            O_POS -> recipient in listOf(O_POS, A_POS, B_POS, AB_POS)
            A_NEG -> recipient in listOf(A_NEG, A_POS, AB_NEG, AB_POS)
            A_POS -> recipient in listOf(A_POS, AB_POS)
            B_NEG -> recipient in listOf(B_NEG, B_POS, AB_NEG, AB_POS)
            B_POS -> recipient in listOf(B_POS, AB_POS)
            AB_NEG -> recipient in listOf(AB_NEG, AB_POS)
            AB_POS -> recipient == AB_POS
        }
    }

    fun canReceiveRbcFrom(donor: BloodGroup): Boolean {
        return donor.canDonateRbcTo(this)
    }

    /**
     * All blood groups that can donate RBC/whole blood to this group.
     */
    fun getCompatibleDonors(): List<BloodGroup> {
        return entries.filter { it.canDonateRbcTo(this) }
    }

    /**
     * All blood groups this group can donate to.
     */
    fun getCompatibleRecipients(): List<BloodGroup> {
        return entries.filter { this.canDonateRbcTo(it) }
    }

    companion object {
        fun fromLabel(label: String): BloodGroup {
            val normalized = label.trim().replace("-", "−")
            return entries.firstOrNull { it.label == normalized || it.label.replace("−", "-") == label.trim() }
                ?: O_POS
        }
    }
}
