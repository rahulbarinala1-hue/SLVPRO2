package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Vehicle
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun VehicleManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val vehicles by dao
        .getVehicles()
        .collectAsState(initial = emptyList())

    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var showForm by remember { mutableStateOf(false) }

    if (showForm) {

        VehicleForm(
            existing = editingVehicle,

            onSave = { vehicle ->

                scope.launch {
                    dao.insertVehicle(vehicle)
                }

                editingVehicle = null
                showForm = false
            },

            onCancel = {
                editingVehicle = null
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
                "Vehicle Management",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    editingVehicle = null
                    showForm = true
                }
            ) {
                Text("Add Vehicle")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                vehicles,
                key = { it.vehicleNo }
            ) { vehicle ->

                Card(
                    Modifier.fillMaxWidth()
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

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    editingVehicle = vehicle
                                    showForm = true
                                }
                            ) {
                                Text("Edit")
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        dao.deleteVehicle(vehicle)
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
private fun VehicleForm(
    existing: Vehicle?,
    onSave: (Vehicle) -> Unit,
    onCancel: () -> Unit
) {

    var vehicleNo by remember {
        mutableStateOf(existing?.vehicleNo ?: "")
    }

    var type by remember {
        mutableStateOf(existing?.type ?: "Truck")
    }

    var model by remember {
        mutableStateOf(existing?.model ?: "")
    }

    var owner by remember {
        mutableStateOf(existing?.owner ?: "SLV Trans")
    }

    var chassisNo by remember {
        mutableStateOf(existing?.chassisNo ?: "")
    }

    var permitType by remember {
        mutableStateOf(existing?.permitType ?: "National")
    }

    var fastagId by remember {
        mutableStateOf(existing?.fastagId ?: "")
    }

    var fastagBalance by remember {
        mutableStateOf(
            existing?.fastagBalance?.toString() ?: "0"
        )
    }

    var pucDays by remember {
        mutableStateOf(daysFromExpiry(existing?.pucExpiry))
    }

    var insuranceDays by remember {
        mutableStateOf(daysFromExpiry(existing?.insuranceExpiry))
    }

    var fitnessDays by remember {
        mutableStateOf(daysFromExpiry(existing?.fitnessExpiry))
    }

    var permitDays by remember {
        mutableStateOf(daysFromExpiry(existing?.permitExpiry))
    }

    var taxDays by remember {
        mutableStateOf(daysFromExpiry(existing?.taxExpiry))
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Text(
                if (existing == null)
                    "Add Vehicle"
                else
                    "Edit Vehicle",
                style = MaterialTheme.typography.headlineSmall
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
                value = type,
                onValueChange = { type = it },
                label = { Text("Type") },
                placeholder = { Text("Truck / Lorry") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
                placeholder = { Text("Tata 5530") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = owner,
                onValueChange = { owner = it },
                label = { Text("Owner") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = chassisNo,
                onValueChange = { chassisNo = it },
                label = { Text("Chassis No.") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                "Document expiry — enter days from today",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            OutlinedTextField(
                value = pucDays,
                onValueChange = { pucDays = it },
                label = { Text("PUC days") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = insuranceDays,
                onValueChange = { insuranceDays = it },
                label = { Text("Insurance days") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = fitnessDays,
                onValueChange = { fitnessDays = it },
                label = { Text("Fitness days") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = permitType,
                onValueChange = { permitType = it },
                label = { Text("Permit type") },
                placeholder = { Text("National / State") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = permitDays,
                onValueChange = { permitDays = it },
                label = { Text("Permit days") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = taxDays,
                onValueChange = { taxDays = it },
                label = { Text("Tax days") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                "FASTag",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            OutlinedTextField(
                value = fastagId,
                onValueChange = { fastagId = it },
                label = { Text("FASTag ID") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = fastagBalance,
                onValueChange = { fastagBalance = it },
                label = { Text("FASTag Balance ₹") },
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

                        val now = System.currentTimeMillis()

                        onSave(
                            Vehicle(
                                vehicleNo = vehicleNo.trim(),
                                type = type,
                                model = model,
                                owner = owner,
                                chassisNo = chassisNo,
                                pucExpiry = expiryFromDays(
                                    pucDays,
                                    now
                                ),
                                insuranceExpiry = expiryFromDays(
                                    insuranceDays,
                                    now
                                ),
                                fitnessExpiry = expiryFromDays(
                                    fitnessDays,
                                    now
                                ),
                                permitType = permitType,
                                permitExpiry = expiryFromDays(
                                    permitDays,
                                    now
                                ),
                                taxExpiry = expiryFromDays(
                                    taxDays,
                                    now
                                ),
                                fastagBalance =
                                    fastagBalance.toIntOrNull()
                                        ?: 0,
                                fastagId = fastagId
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

private fun expiryFromDays(
    value: String,
    now: Long
): Long {

    val days = value.toLongOrNull()
        ?: return 0L

    return now + TimeUnit.DAYS.toMillis(days)
}

private fun daysFromExpiry(
    expiry: Long?
): String {

    if (expiry == null || expiry <= 0L) {
        return ""
    }

    val difference =
        expiry - System.currentTimeMillis()

    return (
        difference /
            TimeUnit.DAYS.toMillis(1)
        ).toString()
}
