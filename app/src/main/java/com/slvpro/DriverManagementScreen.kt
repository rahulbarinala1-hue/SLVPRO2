package com.slvpro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Driver
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

@Composable
fun DriverManagementScreen(
    database: AppDatabase
) {

    val dao = database.fleetDao()

    val drivers by dao
        .getDrivers()
        .collectAsState(initial = emptyList())

    var showAddForm by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Driver Management",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    showAddForm = !showAddForm
                }
            ) {
                Text("Add")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (showAddForm) {

            AddDriverForm(
                database = database,
                onSaved = {
                    showAddForm = false
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = drivers,
                key = { it.driverId }
            ) { driver ->

                DriverCard(
                    driver = driver,
                    onDelete = {
                        dao.deleteDriver(driver)
                    }
                )
            }
        }
    }
}


@Composable
private fun AddDriverForm(
    database: AppDatabase,
    onSaved: () -> Unit
) {

    val dao = database.fleetDao()

    var driverId by remember {
        mutableStateOf("")
    }

    var name by remember {
        mutableStateOf("")
    }

    var licenseNo by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var assignedVehicle by remember {
        mutableStateOf("")
    }

    var expiryDays by remember {
        mutableStateOf("365")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = driverId,
            onValueChange = {
                driverId = it
            },
            label = {
                Text("Driver ID")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Driver Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = licenseNo,
            onValueChange = {
                licenseNo = it
            },
            label = {
                Text("License No.")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
            },
            label = {
                Text("Phone")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = assignedVehicle,
            onValueChange = {
                assignedVehicle = it
            },
            label = {
                Text("Assigned Vehicle")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expiryDays,
            onValueChange = {
                expiryDays = it
            },
            label = {
                Text("License expiry — days from today")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val days =
                    expiryDays.toLongOrNull() ?: 365L

                dao.insertDriver(
                    Driver(
                        driverId = driverId,
                        name = name,
                        licenseNo = licenseNo,
                        phone = phone,
                        licenseExpiry =
                            System.currentTimeMillis() +
                                    TimeUnit.DAYS.toMillis(days),
                        assignedVehicle = assignedVehicle
                    )
                )

                onSaved()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Driver")
        }
    }
}


@Composable
private fun DriverCard(
    driver: Driver,
    onDelete: () -> Unit
) {

    val daysLeft = licenseDaysLeft(
        driver.licenseExpiry
    )

    val warning = daysLeft <= 7

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = driver.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Driver ID: ${driver.driverId}"
            )

            Text(
                text = "License: ${driver.licenseNo}"
            )

            Text(
                text = "Phone: ${driver.phone}"
            )

            Text(
                text = "Vehicle: ${driver.assignedVehicle}"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = when {
                    daysLeft < 0 ->
                        "🔴 License expired ${-daysLeft} days ago"

                    daysLeft == 0 ->
                        "🔴 License expires today"

                    daysLeft <= 7 ->
                        "🟠 License expires in $daysLeft days"

                    else ->
                        "✅ License: $daysLeft days left"
                },
                color =
                    if (warning)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = onDelete
            ) {
                Text("Delete")
            }
        }
    }
}


private fun licenseDaysLeft(
    expiry: Long
): Int {

    val difference =
        expiry - System.currentTimeMillis()

    return ceil(
        difference.toDouble() /
                (24L * 60L * 60L * 1000L)
    ).toInt()
}
