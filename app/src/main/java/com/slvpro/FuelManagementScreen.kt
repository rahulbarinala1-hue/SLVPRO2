package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.FuelLog
import kotlinx.coroutines.launch

@Composable
fun FuelManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val fuelLogs by dao
        .getFuelLogs()
        .collectAsState(initial = emptyList())

    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        FuelForm(
            onSave = { fuelLog ->
                scope.launch {
                    dao.insertFuelLog(fuelLog)
                }
                showForm = false
            },
            onCancel = {
                showForm = false
            }
        )
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                "Fuel Management",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    showForm = true
                }
            ) {
                Text("Add Fuel")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Fuel Entries: ${fuelLogs.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                fuelLogs,
                key = { it.id }
            ) { fuel ->

                Card(
                    Modifier.fillMaxWidth()
                ) {

                    Column(
                        Modifier.padding(16.dp)
                    ) {

                        Text(
                            fuel.vehicleNo,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            "⛽ ${fuel.litres} litres"
                        )

                        Text(
                            "Amount: ₹${fuel.amount}"
                        )

                        Text(
                            "KM Reading: ${fuel.kmReading}"
                        )

                        if (fuel.station.isNotBlank()) {
                            Text(
                                "Station: ${fuel.station}"
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    dao.deleteFuelLog(fuel)
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FuelForm(
    onSave: (FuelLog) -> Unit,
    onCancel: () -> Unit
) {

    var vehicleNo by remember {
        mutableStateOf("")
    }

    var litres by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var kmReading by remember {
        mutableStateOf("")
    }

    var station by remember {
        mutableStateOf("")
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        item {
            Text(
                "Add Fuel Entry",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = vehicleNo,
                onValueChange = {
                    vehicleNo = it
                },
                label = {
                    Text("Vehicle No.")
                },
                placeholder = {
                    Text("KA51 AB 1234")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = litres,
                onValueChange = {
                    litres = it
                },
                label = {
                    Text("Litres")
                },
                placeholder = {
                    Text("150.5")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = {
                    Text("Amount ₹")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = kmReading,
                onValueChange = {
                    kmReading = it
                },
                label = {
                    Text("KM Reading")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = station,
                onValueChange = {
                    station = it
                },
                label = {
                    Text("Fuel Station")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {

                        if (vehicleNo.isBlank()) {
                            return@Button
                        }

                        onSave(
                            FuelLog(
                                vehicleNo =
                                    vehicleNo.trim(),
                                litres =
                                    litres.toFloatOrNull()
                                        ?: 0f,
                                amount =
                                    amount.toIntOrNull()
                                        ?: 0,
                                kmReading =
                                    kmReading.toIntOrNull()
                                        ?: 0,
                                station =
                                    station.trim(),
                                date =
                                    System.currentTimeMillis()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
