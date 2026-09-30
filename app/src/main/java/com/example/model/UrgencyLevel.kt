package com.example.model

enum class UrgencyLevel(val title: String, val subtitle: String, val priority: Int) {
    CRITICAL("Critical", "Immediate transfusion required (< 1 hour)", 1),
    WITHIN_24H("Within 24h", "Transfusion needed within 24 hours", 2),
    PLANNED("Planned", "Scheduled surgery or procedure", 3)
}
