package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DonorEntity
import com.example.model.BloodGroup
import com.example.ui.LifeLinkViewModel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.IntentHelper
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer
import com.example.ui.theme.UrgentCriticalRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorFinderScreen(
    viewModel: LifeLinkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDonors by viewModel.allDonors.collectAsState()
    val targetGroup by viewModel.donorTargetBloodGroup.collectAsState()
    val includeCompatible by viewModel.donorIncludeCompatible.collectAsState()
    val maxDistance by viewModel.donorMaxDistanceKm.collectAsState()
    val onlyAvailable by viewModel.donorOnlyAvailable.collectAsState()
    val consentedIds by viewModel.consentedDonorIds.collectAsState()

    // Compatibility logic:
    // If includeCompatible is true, include any donor who can donate to targetGroup!
    // E.g., for A+, donors can be A+, A-, O+, O-.
    val compatibleGroups = if (includeCompatible) {
        targetGroup.getCompatibleDonors()
    } else {
        listOf(targetGroup)
    }

    val filteredDonors = allDonors.filter { donor ->
        val donorGroup = donor.getBloodGroupEnum()
        val matchesGroup = donorGroup in compatibleGroups
        val matchesDistance = donor.distanceKm <= maxDistance
        val matchesAvailability = !onlyAvailable || donor.isAvailable
        matchesGroup && matchesDistance && matchesAvailability
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Target Recipient Blood Group Selector
            Text(
                text = "Patient Blood Group Needed",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BloodGroup.entries.forEach { group ->
                    val isSelected = group == targetGroup
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.donorTargetBloodGroup.value = group },
                        label = { Text(group.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("donor_group_chip_${group.label}")
                    )
                }
            }
        }

        // Blood Compatibility Explanation Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Blood Compatibility Logic",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (includeCompatible) {
                                    "Showing donors: ${compatibleGroups.joinToString { it.label }}"
                                } else "Exact match only (${targetGroup.label})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Include All Compatible", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = includeCompatible,
                                onCheckedChange = { viewModel.donorIncludeCompatible.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                            )
                        }
                    }

                    if (targetGroup == BloodGroup.O_NEG) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ℹ️ Note: O− can only receive blood from O− donors.",
                            fontSize = 11.sp,
                            color = AlertOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (targetGroup == BloodGroup.AB_POS) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 AB+ is the Universal Recipient (can safely receive blood from any blood group).",
                            fontSize = 11.sp,
                            color = SafeGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Distance & Availability Slider
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Maximum Radius: ${maxDistance.toInt()} km",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Only Available", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = onlyAvailable,
                                onCheckedChange = { viewModel.donorOnlyAvailable.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed)
                            )
                        }
                    }

                    Slider(
                        value = maxDistance,
                        onValueChange = { viewModel.donorMaxDistanceKm.value = it },
                        valueRange = 5f..50f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonRed,
                            activeTrackColor = CrimsonRed
                        )
                    )
                }
            }
        }

        // List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Matching Donors (${filteredDonors.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Privacy Protected",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Donors List
        if (filteredDonors.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No matching donors found in this radius. Try expanding distance slider.")
                    }
                }
            }
        } else {
            items(filteredDonors) { donor ->
                val hasConsent = donor.id in consentedIds
                DonorCard(
                    donor = donor,
                    hasConsent = hasConsent,
                    context = context,
                    onRequestConsent = { viewModel.requestDonorContact(donor.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DonorCard(
    donor: DonorEntity,
    hasConsent: Boolean,
    context: Context,
    onRequestConsent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEligible = donor.isEligible()
    val daysRemaining = donor.daysUntilEligible()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donor_card_${donor.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 44.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = donor.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (donor.isAvailable) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SafeGreen)
                                )
                            }
                        }
                        Text(
                            text = "Age: ${donor.age} • ${donor.city} • ${donor.distanceKm} km away",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isEligible) SafeGreenContainer else RoseContainer
                ) {
                    Text(
                        text = if (isEligible) "ELIGIBLE" else "COOLDOWN",
                        color = if (isEligible) SafeGreen else UrgentCriticalRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cooldown 90-day alert notice
            if (!isEligible) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoseContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = UrgentCriticalRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Donated recently. Next eligible in $daysRemaining days (90-day cooldown).",
                            fontSize = 11.sp,
                            color = CrimsonDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Privacy Notice & Contact Action
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (hasConsent) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (hasConsent) SafeGreen else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasConsent) donor.phone else donor.maskedPhone(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (!hasConsent) {
                        Button(
                            onClick = onRequestConsent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("request_consent_${donor.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Text("Request Contact", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SafeGreenContainer
                        ) {
                            Text(
                                text = "Consent Granted",
                                color = SafeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Direct Call & WhatsApp buttons when consent is granted
            if (hasConsent) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { IntentHelper.dialPhone(context, donor.phone) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("call_donor_${donor.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call Donor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            IntentHelper.openWhatsApp(
                                context,
                                donor.phone,
                                "Hello ${donor.name}, reaching out via LifeLink for an urgent ${donor.bloodGroup} blood requirement. Are you available to donate?"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("whatsapp_donor_${donor.id}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
