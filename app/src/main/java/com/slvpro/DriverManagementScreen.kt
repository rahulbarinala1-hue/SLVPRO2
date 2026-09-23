package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Driver
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun DriverManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val drivers by dao
        .getDrivers()
        .collectAsState(initial = emptyList())

    var showForm by remember { mutableStateOf(false) }
    var editingDriver by remember { mutableStateOf<Driver?>(null) }

    if (showForm) {

        DriverForm(
            existing = editingDriver,

            onSave = { driver ->

                scope.launch {
                    dao.insertDriver(driver)
                }

                editingDriver = null
                showForm = false
            },

            onCancel = {
                editingDriver = null
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
                "Driver Management",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    editingDriver = null
                    showForm = true
                }
            ) {
                Text("Add Driver")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                drivers,
                key = { it.driverId }
            ) { driver ->

                Card(
                    Modifier.fillMaxWidth()
                ) {

                    Column(
                        Modifier.padding(16.dp)
                    ) {

                        Text(
                            driver.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text("Driver ID: ${driver.driverId}")

                        Text("License: ${driver.licenseNo}")

                        Text("Phone: ${driver.phone}")

                        if (driver.assignedVehicle.isNotBlank()) {
                            Text(
                                "Vehicle: ${driver.assignedVehicle}"
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        val daysLeft =
                            calculateDriverDaysLeft(
                                driver.licenseExpiry
                            )

                        if (driver.licenseExpiry > 0L && daysLeft <= 7L) {
    Text(
        when {
            daysLeft < 0L -> "License expired ${-daysLeft} days ago"
            daysLeft == 0L -> "License expires today"
            else -> "License expires in $daysLeft days"
        },
        color = MaterialTheme.colorScheme.error
    )
}

                        Spacer(Modifier.height(8.dp))

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    editingDriver = driver
                                    showForm = true
                                }
                            ) {
                                Text("Edit")
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        dao.deleteDriver(driver)
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
private fun DriverForm(
    existing: Driver?,
    onSave: (Driver) -> Unit,
    onCancel: () -> Unit
) {

    var driverId by remember {
        mutableStateOf(existing?.driverId ?: "")
    }

    var name by remember {
        mutableStateOf(existing?.name ?: "")
    }

    var licenseNo by remember {
        mutableStateOf(existing?.licenseNo ?: "")
    }

    var phone by remember {
        mutableStateOf(existing?.phone ?: "")
    }

    var assignedVehicle by remember {
        mutableStateOf(existing?.assignedVehicle ?: "")
    }

    var licenseDays by remember {
        mutableStateOf(
            driverDaysFromExpiry(
                existing?.licenseExpiry
            )
        )
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
                    "Add Driver"
                else
                    "Edit Driver",
                style = MaterialTheme.typography.headlineSmall
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Driver Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = licenseNo,
                onValueChange = { licenseNo = it },
                label = { Text("License No.") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = assignedVehicle,
                onValueChange = {
                    assignedVehicle = it
                },
                label = { Text("Assigned Vehicle") },
                placeholder = {
                    Text("KA51 AB 1234")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                "License expiry — enter days from today",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            OutlinedTextField(
                value = licenseDays,
                onValueChange = {
                    licenseDays = it
                },
                label = { Text("License days") },
                placeholder = { Text("365") },
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

                        if (driverId.isBlank() ||
                            name.isBlank()
                        ) {
                            return@Button
                        }

                        val now =
                            System.currentTimeMillis()

                        onSave(
                            Driver(
                                driverId =
                                    driverId.trim(),
                                name = name.trim(),
                                licenseNo =
                                    licenseNo.trim(),
                                phone = phone.trim(),
                                licenseExpiry =
                                    driverExpiryFromDays(
                                        licenseDays,
                                        now
                                    ),
                                assignedVehicle =
                                    assignedVehicle.trim()
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

private fun driverExpiryFromDays(
    value: String,
    now: Long
): Long {

    val days = value.toLongOrNull()
        ?: return 0L

    return now +
        TimeUnit.DAYS.toMillis(days)
}

private fun driverDaysFromExpiry(
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

private fun calculateDriverDaysLeft(
    expiry: Long
): Long {

    if (expiry <= 0L) {
        return 0L
    }

    val difference =
        expiry - System.currentTimeMillis()

    return difference /
        TimeUnit.DAYS.toMillis(1)
}
