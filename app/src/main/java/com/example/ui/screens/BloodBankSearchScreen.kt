package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BloodBankEntity
import com.example.model.BloodGroup
import com.example.ui.LifeLinkViewModel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.IntentHelper
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertOrangeContainer
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer
import com.example.ui.theme.UrgentCriticalRed

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BloodBankSearchScreen(
    viewModel: LifeLinkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allBanks by viewModel.allBloodBanks.collectAsState()
    val selectedGroup by viewModel.selectedBankBloodGroup.collectAsState()
    val selectedCity by viewModel.selectedBankCity.collectAsState()
    val selectedComponent by viewModel.selectedBankComponent.collectAsState()
    val searchQuery by viewModel.bankSearchQuery.collectAsState()

    val bloodGroups = listOf("All") + BloodGroup.entries.map { it.label }
    val cities = listOf("All Cities", "Central District", "North Zone", "West Zone", "South Zone")
    val components = listOf("All", "Whole Blood", "Plasma", "Platelets", "RBC")

    val filteredBanks = allBanks.filter { bank ->
        val matchesCity = selectedCity == "All Cities" || bank.city.contains(selectedCity, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                bank.name.contains(searchQuery, ignoreCase = true) ||
                bank.address.contains(searchQuery, ignoreCase = true)
        matchesCity && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.bankSearchQuery.value = it },
                label = { Text("Search by Hospital or Blood Bank name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blood_bank_search_input")
            )
        }

        // Blood Group Filter Chips
        item {
            Text(
                text = "Filter by Blood Group",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                bloodGroups.forEach { group ->
                    val isSelected = (group == "All" && selectedGroup == null) || (group == selectedGroup)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedBankBloodGroup.value = if (group == "All") null else group
                        },
                        label = { Text(group, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("bank_group_chip_$group")
                    )
                }
            }
        }

        // City & Component Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // City Dropdown
                var cityExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = !cityExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Location") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    viewModel.selectedBankCity.value = city
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Component Dropdown
                var compExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = compExpanded,
                    onExpandedChange = { compExpanded = !compExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedComponent,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Component") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = compExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = compExpanded,
                        onDismissRequest = { compExpanded = false }
                    ) {
                        components.forEach { comp ->
                            DropdownMenuItem(
                                text = { Text(comp) },
                                onClick = {
                                    viewModel.selectedBankComponent.value = comp
                                    compExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Results Count Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Found ${filteredBanks.size} Blood Facilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (selectedGroup != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoseContainer
                    ) {
                        Text(
                            text = "Showing units for $selectedGroup",
                            color = CrimsonDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Blood Banks List
        if (filteredBanks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No blood banks found matching the criteria.")
                    }
                }
            }
        } else {
            items(filteredBanks) { bank ->
                BloodBankCard(
                    bank = bank,
                    selectedBloodGroup = selectedGroup,
                    selectedComponent = selectedComponent,
                    context = context
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BloodBankCard(
    bank: BloodBankEntity,
    selectedBloodGroup: String?,
    selectedComponent: String,
    context: Context,
    modifier: Modifier = Modifier
) {
    val groupUnits = if (selectedBloodGroup != null) {
        bank.getStockForGroup(selectedBloodGroup)
    } else {
        bank.getTotalUnits()
    }

    val isLowStock = if (selectedBloodGroup != null) groupUnits < 3 else bank.hasLowStock()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("blood_bank_card_${bank.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bank.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bank.facilityType,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${bank.distanceKm} km away",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address & Last Updated
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${bank.address}, ${bank.city}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = bank.lastUpdatedText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Highlight Card
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLowStock) AlertOrangeContainer else SafeGreenContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLowStock) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AlertOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SafeGreen)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedBloodGroup != null) {
                                "$selectedBloodGroup Availability:"
                            } else "Total Units Available:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isLowStock) AlertOrange else SafeGreen
                        )
                    }

                    Text(
                        text = "$groupUnits Units Available",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = if (isLowStock) AlertOrange else SafeGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Group Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("O−", bank.stockONeg),
                    Pair("O+", bank.stockOPos),
                    Pair("A+", bank.stockAPos),
                    Pair("B+", bank.stockBPos),
                    Pair("Platelets", bank.plateletUnits)
                ).forEach { (label, count) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$count",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (count < 3) UrgentCriticalRed else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Call & Get Directions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { IntentHelper.dialPhone(context, bank.phone) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("call_bank_button_${bank.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Bank", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = {
                        IntentHelper.openDirections(
                            context,
                            bank.latitude,
                            bank.longitude,
                            bank.name
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("directions_bank_button_${bank.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directions", fontSize = 13.sp)
                }
            }
        }
    }
}
