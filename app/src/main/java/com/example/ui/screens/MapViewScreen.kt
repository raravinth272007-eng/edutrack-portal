package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BloodBankEntity
import com.example.data.entity.DonorEntity
import com.example.ui.LifeLinkViewModel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.IntentHelper
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer

sealed class MapItem {
    data class Bank(val bank: BloodBankEntity) : MapItem()
    data class Donor(val donor: DonorEntity) : MapItem()
}

@Composable
fun MapViewScreen(
    viewModel: LifeLinkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bloodBanks by viewModel.allBloodBanks.collectAsState()
    val donors by viewModel.allDonors.collectAsState()

    var filterMode by remember { mutableStateOf("All") } // "All", "Banks", "Donors"
    var selectedItem by remember { mutableStateOf<MapItem?>(null) }

    // Center coordinate of our map (approx Central District: 28.6139, 77.2090)
    val centerLat = 28.6139
    val centerLng = 77.2090
    val latSpan = 0.12
    val lngSpan = 0.12

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Chips Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("All", "Blood Banks", "Donors").forEach { mode ->
                val isSelected = filterMode == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { filterMode = mode },
                    label = { Text(mode, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrimsonRed,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Live GPS Radius",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Map Canvas Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2E8F0))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(filterMode, bloodBanks, donors) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val height = size.height

                            // Check bank hits
                            val tappedBank = bloodBanks.firstOrNull { bank ->
                                if (filterMode == "Donors") return@firstOrNull false
                                val x = ((bank.longitude - (centerLng - lngSpan / 2)) / lngSpan * width).toFloat()
                                val y = (((centerLat + latSpan / 2) - bank.latitude) / latSpan * height).toFloat()
                                val dist = kotlin.math.hypot(offset.x - x, offset.y - y)
                                dist < 36f
                            }

                            if (tappedBank != null) {
                                selectedItem = MapItem.Bank(tappedBank)
                                return@detectTapGestures
                            }

                            // Check donor hits
                            val tappedDonor = donors.firstOrNull { donor ->
                                if (filterMode == "Blood Banks") return@firstOrNull false
                                val x = ((donor.longitude - (centerLng - lngSpan / 2)) / lngSpan * width).toFloat()
                                val y = (((centerLat + latSpan / 2) - donor.latitude) / latSpan * height).toFloat()
                                val dist = kotlin.math.hypot(offset.x - x, offset.y - y)
                                dist < 36f
                            }

                            if (tappedDonor != null) {
                                selectedItem = MapItem.Donor(tappedDonor)
                                return@detectTapGestures
                            }

                            selectedItem = null
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw map grid and streets simulation
                val gridColor = Color(0xFFCBD5E1)
                for (i in 1..7) {
                    val x = canvasWidth * i / 8f
                    drawLine(gridColor, Offset(x, 0f), Offset(x, canvasHeight), strokeWidth = 1.5f)
                }
                for (j in 1..7) {
                    val y = canvasHeight * j / 8f
                    drawLine(gridColor, Offset(0f, y), Offset(canvasWidth, y), strokeWidth = 1.5f)
                }

                // Draw central distance rings (5km, 10km, 15km)
                val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)
                drawCircle(
                    color = Color(0x332563EB),
                    radius = canvasWidth * 0.18f,
                    center = centerOffset,
                    style = Stroke(width = 2f)
                )
                drawCircle(
                    color = Color(0x222563EB),
                    radius = canvasWidth * 0.35f,
                    center = centerOffset,
                    style = Stroke(width = 2f)
                )

                // Draw User Current Location (Blue pulse)
                drawCircle(color = Color(0x442563EB), radius = 24f, center = centerOffset)
                drawCircle(color = Color(0xFF2563EB), radius = 10f, center = centerOffset)
                drawCircle(color = Color.White, radius = 4f, center = centerOffset)

                // Draw Blood Banks (Red Hospital Pins)
                if (filterMode != "Donors") {
                    bloodBanks.forEach { bank ->
                        val x = ((bank.longitude - (centerLng - lngSpan / 2)) / lngSpan * canvasWidth).toFloat()
                        val y = (((centerLat + latSpan / 2) - bank.latitude) / latSpan * canvasHeight).toFloat()
                        val pinOffset = Offset(x.coerceIn(24f, canvasWidth - 24f), y.coerceIn(24f, canvasHeight - 24f))

                        // Outer ring
                        drawCircle(color = Color(0x55DC2626), radius = 20f, center = pinOffset)
                        // Inner marker
                        drawCircle(color = CrimsonRed, radius = 13f, center = pinOffset)
                        drawCircle(color = Color.White, radius = 5f, center = pinOffset)
                    }
                }

                // Draw Donors (Green Voluntary Pins)
                if (filterMode != "Blood Banks") {
                    donors.forEach { donor ->
                        val x = ((donor.longitude - (centerLng - lngSpan / 2)) / lngSpan * canvasWidth).toFloat()
                        val y = (((centerLat + latSpan / 2) - donor.latitude) / latSpan * canvasHeight).toFloat()
                        val pinOffset = Offset(x.coerceIn(24f, canvasWidth - 24f), y.coerceIn(24f, canvasHeight - 24f))

                        drawCircle(color = Color(0x4416A34A), radius = 16f, center = pinOffset)
                        drawCircle(color = SafeGreen, radius = 10f, center = pinOffset)
                        drawCircle(color = Color.White, radius = 4f, center = pinOffset)
                    }
                }
            }

            // Map Legend Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("You", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Blood Banks", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SafeGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Donors", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recenter Button
            IconButton(
                onClick = { selectedItem = null },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Selected Item Bottom Overlay Sheet
            if (selectedItem != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp)
                ) {
                    selectedItem?.let { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("map_selected_card")
                    ) {
                        when (item) {
                            is MapItem.Bank -> {
                                val bank = item.bank
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = bank.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${bank.distanceKm} km away • ${bank.city}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        Surface(
                                            color = RoseContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${bank.getTotalUnits()} Units",
                                                fontWeight = FontWeight.Bold,
                                                color = CrimsonRed,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { IntentHelper.dialPhone(context, bank.phone) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Call Bank", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { IntentHelper.openDirections(context, bank.latitude, bank.longitude, bank.name) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Directions", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                            is MapItem.Donor -> {
                                val donor = item.donor
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BloodGroupBadge(bloodGroup = donor.bloodGroup, size = 36.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = donor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(
                                                text = "${donor.city} • ${donor.distanceKm} km away",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        Surface(
                                            color = if (donor.isEligible()) SafeGreenContainer else RoseContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (donor.isEligible()) "ELIGIBLE" else "COOLDOWN",
                                                color = if (donor.isEligible()) SafeGreen else CrimsonRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            viewModel.requestDonorContact(donor.id)
                                            IntentHelper.dialPhone(context, donor.phone)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Request Contact & Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
