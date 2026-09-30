package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.EmergencyRequestEntity
import com.example.model.BloodGroup
import com.example.model.UrgencyLevel
import com.example.ui.EmergencyFormState
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.UrgentCriticalRed
import com.example.ui.theme.UrgentPlannedBlue
import com.example.ui.theme.UrgentWarningAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmergencyRequestBottomSheet(
    formState: EmergencyFormState,
    onFormChange: ((EmergencyFormState) -> EmergencyFormState) -> Unit,
    onDismiss: () -> Unit,
    onSubmitBroadcast: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bloodGroups = BloodGroup.entries.map { it.label }
    val cities = listOf("Central District", "North Zone", "West Zone", "South Zone", "Metro East")
    val components = listOf("Whole Blood", "Plasma (FFP)", "Platelets (SDP)", "Packed RBC (PRBC)")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoseContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = UrgentCriticalRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "New Emergency Request",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Broadcasts instant SOS to matching nearby donors",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_emergency_modal")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blood Group Selector
            Text(
                text = "Required Blood Group *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                bloodGroups.forEach { bg ->
                    val isSelected = formState.bloodGroup == bg
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFormChange { it.copy(bloodGroup = bg) } },
                        label = {
                            Text(
                                text = bg,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("group_chip_$bg")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Urgency Level Selector
            Text(
                text = "Urgency Level *",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UrgencyLevel.entries.forEach { level ->
                    val isSelected = formState.urgency == level
                    val (color, borderColor) = when (level) {
                        UrgencyLevel.CRITICAL -> Pair(UrgentCriticalRed, RoseContainer)
                        UrgencyLevel.WITHIN_24H -> Pair(UrgentWarningAmber, Color(0xFFFEF3C7))
                        UrgencyLevel.PLANNED -> Pair(UrgentPlannedBlue, Color(0xFFE0F2FE))
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onFormChange { it.copy(urgency = level) } },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = level.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Units Needed Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Units Needed",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "1 unit ~ 350-450 mL",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (formState.unitsNeeded > 1) {
                                onFormChange { it.copy(unitsNeeded = it.unitsNeeded - 1) }
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "${formState.unitsNeeded} Units",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = {
                            if (formState.unitsNeeded < 10) {
                                onFormChange { it.copy(unitsNeeded = it.unitsNeeded + 1) }
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Component
            Text(
                text = "Component Type",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                components.forEach { comp ->
                    val isSelected = formState.component == comp
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFormChange { it.copy(component = comp) } },
                        label = { Text(text = comp, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Patient Name
            OutlinedTextField(
                value = formState.patientName,
                onValueChange = { value -> onFormChange { it.copy(patientName = value) } },
                label = { Text("Patient Full Name *") },
                placeholder = { Text("e.g. John Doe") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hospital Name
            OutlinedTextField(
                value = formState.hospitalName,
                onValueChange = { value -> onFormChange { it.copy(hospitalName = value) } },
                label = { Text("Hospital / Medical Facility Name *") },
                placeholder = { Text("e.g. Apex Hospital, ICU Room 4") },
                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hospital_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // City Selection
            var cityExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { cityExpanded = !cityExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = formState.city,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("City / Zone *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onFormChange { it.copy(city = city) }
                                cityExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Phone
            OutlinedTextField(
                value = formState.contactPhone,
                onValueChange = { value -> onFormChange { it.copy(contactPhone = value) } },
                label = { Text("Attendant / Requester Contact Number *") },
                placeholder = { Text("+1 555-010-0000") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_phone_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Additional Notes
            OutlinedTextField(
                value = formState.notes,
                onValueChange = { value -> onFormChange { it.copy(notes = value) } },
                label = { Text("Emergency Notes / Ward / Case details") },
                placeholder = { Text("e.g., Surgery scheduled in 2 hours, please call immediately.") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Broadcast Button
            val isFormValid = formState.patientName.isNotBlank() &&
                    formState.hospitalName.isNotBlank() &&
                    formState.contactPhone.isNotBlank()

            Button(
                onClick = onSubmitBroadcast,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_broadcast_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonRed,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.ShareLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🚨 BROADCAST SOS EMERGENCY ALERT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun BroadcastSuccessDialog(
    request: EmergencyRequestEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(RoseContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📡", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SOS Broadcast Sent!",
                    fontWeight = FontWeight.Bold,
                    color = CrimsonRed
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Emergency alert for ${request.bloodGroup} (${request.unitsNeeded} units) is now live.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Matching Donors Alerted: ${request.matchedDonorsCount} in ${request.city}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nearby Blood Banks notified: Central Metro & City Apex",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Donors can now view this request and contact: ${request.contactPhone}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                modifier = Modifier.testTag("dismiss_broadcast_success")
            ) {
                Text("View Active Alert")
            }
        }
    )
}
