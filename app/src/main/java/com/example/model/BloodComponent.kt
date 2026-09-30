package com.example.model

enum class BloodComponent(val displayName: String, val shortCode: String, val shelfLifeDays: Int) {
    WHOLE_BLOOD("Whole Blood", "WB", 35),
    RBC("Packed Red Blood Cells (PRBC)", "PRBC", 42),
    PLATELETS("Platelets (SDP / RDP)", "Platelets", 5),
    PLASMA("Fresh Frozen Plasma (FFP)", "FFP", 365)
}
