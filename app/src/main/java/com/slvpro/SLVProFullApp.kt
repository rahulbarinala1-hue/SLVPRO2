package com.slvpro

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Vehicle

@Composable
fun SLVProFullApp() {

    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedTab = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableIntStateOf(0)
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
                        text = "SLV-PRO",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {

                tabs.forEachIndexed { index, title ->

                    NavigationBarItem(
                        selected = selectedTab.intValue == index,
                        onClick = {
                            selectedTab.intValue = index
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

        when (selectedTab.intValue) {

            0 -> FleetListScreen(
                context = context,
                paddingValues = paddingValues
            )

            1 -> SimpleComingScreen(
                title = "📦 Trips / LR Management"
            )

            2 -> SimpleComingScreen(
                title = "⛽ Fuel Log"
            )

            3 -> SimpleComingScreen(
                title = "💰 Expense Tracker"
            )

            4 -> SimpleComingScreen(
                title = "📊 Reports & Analytics"
            )
        }
    }
}


@Composable
fun FleetListScreen(
    context: Context,
    paddingValues: PaddingValues
) {

    val dao = AppDatabase
        .getInstance(context)
        .fleetDao()

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
                text = "🚛 SLV Professional Fleet",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        item {

            if (vehicles.any { hasExpiryWithin7Days(it) }) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Expiry warning"
                        )

                        Spacer(
                            modifier = Modifier.padding(6.dp)
                        )

                        Column {

                            Text(
                                text = "⚠️ DOCUMENT EXPIRY WARNING",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Vehicle documents expire within 7 days."
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
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "⚠️ FASTag LOW",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "One or more vehicles need FASTag recharge."
                        )
                    }
                }
            }
        }

        item {

            Text(
                text = "Vehicles (${vehicles.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = vehicles,
            key = { it.vehicleNo }
        ) { vehicle ->

            VehicleCard(vehicle)
        }
    }
}


@Composable
fun VehicleCard(
    vehicle: Vehicle
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = vehicle.vehicleNo,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "${vehicle.type} • ${vehicle.model}"
            )

            Text(
                text = "Owner: ${vehicle.owner}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (vehicle.fastagBalance < 500) {

                Text(
                    text = "⚠️ LOW - Recharge!",
                    color = MaterialTheme.colorScheme.error
                )

            } else {

                Text(
                    text = "✅ FASTag OK"
                )
            }
        }
    }
}


@Composable
fun SimpleComingScreen(
    title: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "SLV-PRO module"
        )
    }
}


private fun hasExpiryWithin7Days(
    vehicle: Vehicle
): Boolean {

    val now = System.currentTimeMillis()

    val sevenDays =
        now + (7L * 24L * 60L * 60L * 1000L)

    return listOf(
        vehicle.pucExpiry,
        vehicle.insuranceExpiry,
        vehicle.fitnessExpiry,
        vehicle.permitExpiry
    ).any { expiry ->

        expiry > 0L && expiry <= sevenDays
    }
}
