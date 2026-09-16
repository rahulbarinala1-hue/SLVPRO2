package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Trip
import kotlinx.coroutines.launch

@Composable
fun TripManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val trips by dao
        .getTrips()
        .collectAsState(initial = emptyList())

    var showForm by remember { mutableStateOf(false) }
    var editingTrip by remember { mutableStateOf<Trip?>(null) }

    if (showForm) {

        TripForm(
            existing = editingTrip,

            onSave = { trip ->

                scope.launch {
                    dao.insertTrip(trip)
                }

                editingTrip = null
                showForm = false
            },

            onCancel = {
                editingTrip = null
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
                "Trips / LR Management",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    editingTrip = null
                    showForm = true
                }
            ) {
                Text("Add Trip")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Total Trips: ${trips.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                trips,
                key = { it.lrNo }
            ) { trip ->

                Card(
                    Modifier.fillMaxWidth()
                ) {

                    Column(
                        Modifier.padding(16.dp)
                    ) {

                        Text(
                            "LR: ${trip.lrNo}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            "${trip.fromLocation} → ${trip.toLocation}"
                        )

                        Text(
                            "Vehicle: ${trip.vehicleNo}"
                        )

                        Text(
                            "Driver: ${trip.driverId}"
                        )

                        Text(
                            "Freight: ₹${trip.freight}"
                        )

                        Text(
                            "Advance: ₹${trip.advance}"
                        )

                        Text(
                            "Status: ${trip.status}"
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    editingTrip = trip
                                    showForm = true
                                }
                            ) {
                                Text("Edit")
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        dao.deleteTrip(trip)
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
}

@Composable
private fun TripForm(
    existing: Trip?,
    onSave: (Trip) -> Unit,
    onCancel: () -> Unit
) {

    var lrNo by remember {
        mutableStateOf(existing?.lrNo ?: "")
    }

    var vehicleNo by remember {
        mutableStateOf(existing?.vehicleNo ?: "")
    }

    var driverId by remember {
        mutableStateOf(existing?.driverId ?: "")
    }

    var fromLocation by remember {
        mutableStateOf(existing?.fromLocation ?: "")
    }

    var toLocation by remember {
        mutableStateOf(existing?.toLocation ?: "")
    }

    var freight by remember {
        mutableStateOf(
            existing?.freight?.toString() ?: "0"
        )
    }

    var advance by remember {
        mutableStateOf(
            existing?.advance?.toString() ?: "0"
        )
    }

    var status by remember {
        mutableStateOf(existing?.status ?: "Ongoing")
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
                if (existing == null)
                    "Add Trip / LR"
                else
                    "Edit Trip / LR",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = lrNo,
                onValueChange = { lrNo = it },
                label = { Text("LR No.") },
                placeholder = { Text("LR001") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = vehicleNo,
                onValueChange = { vehicleNo = it },
                label = { Text("Vehicle No.") },
                placeholder = { Text("KA51 AB 1234") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = driverId,
                onValueChange = { driverId = it },
                label = { Text("Driver ID") },
                placeholder = { Text("D001") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = fromLocation,
                onValueChange = {
                    fromLocation = it
                },
                label = { Text("From") },
                placeholder = { Text("Bangalore") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = toLocation,
                onValueChange = {
                    toLocation = it
                },
                label = { Text("To") },
                placeholder = { Text("Mumbai") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = freight,
                onValueChange = { freight = it },
                label = { Text("Freight ₹") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = advance,
                onValueChange = { advance = it },
                label = { Text("Advance ₹") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                "Trip Status",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = status == "Ongoing",
                    onClick = {
                        status = "Ongoing"
                    },
                    label = {
                        Text("Ongoing")
                    }
                )

                FilterChip(
                    selected = status == "Completed",
                    onClick = {
                        status = "Completed"
                    },
                    label = {
                        Text("Completed")
                    }
                )
            }
        }

        item {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {

                        if (lrNo.isBlank()) {
                            return@Button
                        }

                        onSave(
                            Trip(
                                lrNo = lrNo.trim(),
                                vehicleNo = vehicleNo.trim(),
                                driverId = driverId.trim(),
                                fromLocation =
                                    fromLocation.trim(),
                                toLocation =
                                    toLocation.trim(),
                                freight =
                                    freight.toIntOrNull() ?: 0,
                                advance =
                                    advance.toIntOrNull() ?: 0,
                                startDate =
                                    existing?.startDate
                                        ?: System.currentTimeMillis(),
                                status = status
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
