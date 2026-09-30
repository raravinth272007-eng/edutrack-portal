package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.AppTab
import com.example.ui.LifeLinkViewModel
import com.example.ui.LocalizationManager
import com.example.ui.components.BroadcastSuccessDialog
import com.example.ui.components.EmergencyRequestBottomSheet
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BloodBankSearchScreen
import com.example.ui.screens.DonorFinderScreen
import com.example.ui.screens.DonorHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MapViewScreen
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.LifeLinkTheme
import com.example.ui.theme.RoseContainer

class MainActivity : ComponentActivity() {
    private val viewModel: LifeLinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeLinkTheme {
                LifeLinkApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeLinkApp(viewModel: LifeLinkViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.language.collectAsState()
    val strings = LocalizationManager.get(language)
    val showEmergencyModal by viewModel.showEmergencyModal.collectAsState()
    val emergencyForm by viewModel.emergencyForm.collectAsState()
    val broadcastSuccess by viewModel.broadcastSuccess.collectAsState()
    val activeRequests by viewModel.activeRequests.collectAsState()

    // Handle Back Press when not on HOME tab
    if (currentTab != AppTab.HOME) {
        BackHandler {
            viewModel.setTab(AppTab.HOME)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = strings.appTitle,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = CrimsonDark
                            )
                        }
                    }
                },
                actions = {
                    // Quick SOS Shortcut
                    IconButton(
                        onClick = { viewModel.openEmergencyModal() },
                        modifier = Modifier.testTag("topbar_sos_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (activeRequests.isNotEmpty()) {
                                    Badge(containerColor = CrimsonRed) {
                                        Text("${activeRequests.size}", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = "Emergency SOS",
                                tint = CrimsonRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Language Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.toggleLanguage() }
                            .testTag("language_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Language",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.ENGLISH) "हिन्दी" else "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    NavItem(AppTab.HOME, strings.allBloodGroups.take(4), Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
                    NavItem(AppTab.BLOOD_BANKS, strings.bloodBanksTab, Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital, "nav_banks"),
                    NavItem(AppTab.FIND_DONORS, strings.findDonorsTab, Icons.Filled.People, Icons.Outlined.People, "nav_donors"),
                    NavItem(AppTab.MAP, strings.mapTab, Icons.Filled.Map, Icons.Outlined.Map, "nav_map"),
                    NavItem(AppTab.DONOR_HUB, strings.donorHubTab, Icons.Filled.Person, Icons.Outlined.Person, "nav_profile"),
                    NavItem(AppTab.ADMIN, strings.adminTab, Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_admin"),
                    NavItem(AppTab.LEARN, strings.learnTab.take(4), Icons.Filled.Info, Icons.Outlined.Info, "nav_learn")
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRed,
                            indicatorColor = RoseContainer
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(viewModel = viewModel)
                AppTab.BLOOD_BANKS -> BloodBankSearchScreen(viewModel = viewModel)
                AppTab.FIND_DONORS -> DonorFinderScreen(viewModel = viewModel)
                AppTab.MAP -> MapViewScreen(viewModel = viewModel)
                AppTab.DONOR_HUB -> DonorHubScreen(viewModel = viewModel)
                AppTab.ADMIN -> AdminPanelScreen(viewModel = viewModel)
                AppTab.LEARN -> LearnScreen(viewModel = viewModel)
            }

            // Emergency Bottom Sheet Modal
            if (showEmergencyModal) {
                EmergencyRequestBottomSheet(
                    formState = emergencyForm,
                    onFormChange = { transform -> viewModel.updateEmergencyForm(transform) },
                    onDismiss = { viewModel.closeEmergencyModal() },
                    onSubmitBroadcast = { viewModel.submitEmergencyBroadcast() }
                )
            }

            // Broadcast Success Dialog
            broadcastSuccess?.let { req ->
                BroadcastSuccessDialog(
                    request = req,
                    onDismiss = { viewModel.dismissBroadcastSuccess() }
                )
            }
        }
    }
}

private data class NavItem(
    val tab: AppTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)
