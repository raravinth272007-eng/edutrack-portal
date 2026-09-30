package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DonorEntity
import com.example.model.BloodGroup
import com.example.ui.DonorRegistrationFormState
import com.example.ui.LifeLinkViewModel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertOrangeContainer
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorHubScreen(
    viewModel: LifeLinkViewModel,
    modifier: Modifier = Modifier
) {
    val allDonors by viewModel.allDonors.collectAsState()
    val currentDonorId by viewModel.currentDonorId.collectAsState()
    val myRecords by viewModel.donorHistory.collectAsState()
    val regMessage by viewModel.registrationSuccessMessage.collectAsState()

    val currentDonor = allDonors.firstOrNull { it.id == currentDonorId } ?: allDonors.firstOrNull()

    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Profile & History, 1 = Register / New Donor

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Tab Switcher: "My Profile & History" vs "Register New Donor"
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = CrimsonRed,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text("Donor Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    modifier = Modifier.testTag("tab_donor_dashboard")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text("Register Donor", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    modifier = Modifier.testTag("tab_register_donor")
                )
            }
        }

        if (selectedSubTab == 0) {
            // Dashboard View
            if (currentDonor != null) {
                item {
                    DonorProfileCard(
                        donor = currentDonor,
                        onToggleAvailability = { viewModel.toggleMyAvailability(it) }
                    )
                }

                // 90-Day Eligibility Progress Card
                item {
                    EligibilityCooldownCard(donor = currentDonor)
                }

                // Impact & Hero Badge
                item {
                    val livesSaved = maxOf(1, currentDonor.totalDonations * 3)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Estimated Lives Impacted",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$livesSaved Lives Saved",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Badge: Champion Lifesaver (Tier 2)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRed
                                )
                            }
                        }
                    }
                }

                // Donation History Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Donation Records & Certifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${myRecords.size} Donations",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Records List
                if (myRecords.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No past donations logged yet. Ready to make your first donation!")
                            }
                        }
                    }
                } else {
                    items(myRecords) { record ->
                        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(record.dateMillis))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SafeGreenContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CardMembership, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${record.units} unit (${record.component})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = record.bloodBankName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Surface(color = SafeGreenContainer, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "Verified",
                                            color = SafeGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Registration Form
            item {
                DonorRegistrationForm(
                    onRegister = { form ->
                        viewModel.registerDonor(form)
                        selectedSubTab = 0
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DonorProfileCard(
    donor: DonorEntity,
    onToggleAvailability: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 52.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = donor.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Donor",
                            tint = SafeGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Phone: ${donor.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Zone: ${donor.city} • Age: ${donor.age}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Availability Switch
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (donor.isAvailable) SafeGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (donor.isAvailable) "Available for Emergency SOS" else "Temporarily Unavailable",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (donor.isAvailable) SafeGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (donor.isAvailable) "Receiving urgent alerts in your area" else "Paused notifications",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Switch(
                        checked = donor.isAvailable,
                        onCheckedChange = onToggleAvailability,
                        colors = SwitchDefaults.colors(checkedThumbColor = SafeGreen)
                    )
                }
            }
        }
    }
}

@Composable
fun EligibilityCooldownCard(
    donor: DonorEntity,
    modifier: Modifier = Modifier
) {
    val isEligible = donor.isEligible()
    val daysRemaining = donor.daysUntilEligible()
    val daysPassed = 90 - daysRemaining
    val progress = (daysPassed / 90f).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEligible) SafeGreenContainer else AlertOrangeContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "90-Day Donation Cooldown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isEligible) SafeGreen else AlertOrange
                )
                Text(
                    text = if (isEligible) "Ready to Donate" else "$daysRemaining Days Remaining",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = if (isEligible) SafeGreen else AlertOrange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (isEligible) 1f else progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isEligible) SafeGreen else AlertOrange,
                trackColor = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isEligible) {
                    "✓ You meet the 90-day minimum safety gap between whole blood donations."
                } else {
                    "Medical guidelines require 90 days between donations for complete red cell regeneration."
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorRegistrationForm(
    onRegister: (DonorRegistrationFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("26") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var city by remember { mutableStateOf("Central District") }
    var phone by remember { mutableStateOf("") }
    var daysSinceDonation by remember { mutableStateOf("120") }
    var isAvailable by remember { mutableStateOf(true) }
    var consentAccepted by remember { mutableStateOf(true) }

    val bloodGroups = BloodGroup.entries.map { it.label }
    val cities = listOf("Central District", "North Zone", "West Zone", "South Zone")

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Join LifeLink Donor Registry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Help save lives during acute blood shortages in your city",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Blood Group Selection
            Text(text = "Blood Group *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                bloodGroups.forEach { bg ->
                    val isSelected = bloodGroup == bg
                    FilterChip(
                        selected = isSelected,
                        onClick = { bloodGroup = bg },
                        label = { Text(bg) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age (18-65) *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reg_age_input")
                )
                OutlinedTextField(
                    value = daysSinceDonation,
                    onValueChange = { daysSinceDonation = it },
                    label = { Text("Days Since Last Donation") },
                    placeholder = { Text("0 for first time") },
                    singleLine = true,
                    modifier = Modifier.weight(1.5f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number *") },
                placeholder = { Text("+1 555-010-0000") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_phone_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Primary Zone / City *", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cities.forEach { c ->
                    FilterChip(
                        selected = city == c,
                        onClick = { city = c },
                        label = { Text(c, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Privacy Notice and Consent Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { consentAccepted = !consentAccepted },
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = consentAccepted,
                    onCheckedChange = { consentAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = CrimsonRed),
                    modifier = Modifier.testTag("reg_consent_checkbox")
                )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "I consent to emergency contact via LifeLink.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "My phone number will stay masked until I accept an emergency request. I confirm I am medically eligible (>50kg, no chronic illness).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val canSubmit = name.isNotBlank() && phone.isNotBlank() && consentAccepted
            Button(
                onClick = {
                    onRegister(
                        DonorRegistrationFormState(
                            name = name,
                            age = age,
                            bloodGroup = bloodGroup,
                            city = city,
                            phone = phone,
                            lastDonationDaysAgo = daysSinceDonation,
                            isAvailable = isAvailable,
                            consentAccepted = consentAccepted
                        )
                    )
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("reg_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                Icon(Icons.Default.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register as Volunteer Donor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
