package com.slvpro

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Vehicle

@Composable
fun SLVProFullApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = AppDatabase.getInstance(context)

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var showVehicleManagement by remember { mutableStateOf(false) }
    var showDriverManagement by remember { mutableStateOf(false) }

    if (selectedVehicle != null) {
        VehicleDetailScreen(
            vehicle = selectedVehicle!!,
            context = context,
            onBack = { selectedVehicle = null }
        )
        return
    }

    if (showVehicleManagement) {
        VehicleManagementScreen(database)
        return
    }
if (showDriverManagement) {
    DriverManagementScreen(database)
    return
}
    val tabs = listOf(
        "Fleet",
        "Trips",
        "Fuel",
        "Expense",
        "Reports"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SLV-PRO",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {

    if (selectedTab == 0) {

        IconButton(
            onClick = {
                showVehicleManagement = true
            }
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Vehicle Management"
            )
        }

        IconButton(
            onClick = {
                showDriverManagement = true
            }
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Driver Management"
            )
        }
    }
}
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            when (index) {
                                0 -> Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = "Fleet"
                                )

                                1 -> Icon(
                                    Icons.Default.LocalShipping,
                                    contentDescription = "Trips"
                                )

                                2 -> Icon(
                                    Icons.Default.LocalGasStation,
                                    contentDescription = "Fuel"
                                )

                                3 -> Icon(
                                    Icons.Default.Money,
                                    contentDescription = "Expense"
                                )

                                else -> Icon(
                                    Icons.Default.Assessment,
                                    contentDescription = "Reports"
                                )
                            }
                        },
                        label = {
                            Text(title)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        when (selectedTab) {
            0 -> FleetListScreen(
                context = context,
                paddingValues = paddingValues,
                onVehicleClick = { vehicle ->
                    selectedVehicle = vehicle
                }
            )

            1 -> TripManagementScreen(database)

            2 -> SimpleComingScreen("⛽ Fuel Log")

            3 -> SimpleComingScreen("💰 Expense Tracker")

            4 -> SimpleComingScreen("📊 Reports & Analytics")
        }
    }
}

@Composable
fun FleetListScreen(
    context: Context,
    paddingValues: PaddingValues,
    onVehicleClick: (Vehicle) -> Unit
) {
    val dao = AppDatabase.getInstance(context).fleetDao()

    val vehicles by dao
        .getVehicles()
        .collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                "🚛 SLV Professional Fleet",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))
        }

        item {
            if (vehicles.any { hasExpiryWithin7Days(it) }) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Expiry warning"
                        )

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                "⚠️ DOCUMENT EXPIRY WARNING",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "Vehicle documents expire within 7 days."
                            )
                        }
                    }
                }
            }
        }

        item {
            if (vehicles.any { it.fastagBalance < 500 }) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Column(
                        Modifier.padding(16.dp)
                    ) {

                        Text(
                            "⚠️ FASTag LOW",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "One or more vehicles need FASTag recharge."
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Vehicles (${vehicles.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = vehicles,
            key = { it.vehicleNo }
        ) { vehicle ->

            VehicleCard(
                vehicle = vehicle,
                modifier = Modifier.clickable {
                    onVehicleClick(vehicle)
                }
            )
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {

        Column(
            Modifier.padding(16.dp)
        ) {

            Text(
                vehicle.vehicleNo,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                "${vehicle.type} • ${vehicle.model}"
            )

            Text(
                "Owner: ${vehicle.owner}"
            )

            Spacer(Modifier.height(8.dp))

            if (vehicle.fastagBalance < 500) {

                Text(
                    "⚠️ LOW - Recharge!",
                    color = MaterialTheme.colorScheme.error
                )

            } else {

                Text("✅ FASTag OK")
            }
        }
    }
}

@Composable
fun SimpleComingScreen(title: String) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        Text("SLV-PRO module")
    }
}

private fun hasExpiryWithin7Days(
    vehicle: Vehicle
): Boolean {

    val now = System.currentTimeMillis()

    val sevenDays =
        now + 7L * 24L * 60L * 60L * 1000L

    return listOf(
        vehicle.pucExpiry,
        vehicle.insuranceExpiry,
        vehicle.fitnessExpiry,
        vehicle.permitExpiry
    ).any { expiry ->
        expiry > 0L && expiry <= sevenDays
    }
}
