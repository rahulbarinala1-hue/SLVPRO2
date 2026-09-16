package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Maintenance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaintenanceManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val maintenanceList by dao.getMaintenance()
        .collectAsState(initial = emptyList())

    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        MaintenanceForm(
            onSave = { maintenance ->
                scope.launch {
                    dao.insertMaintenance(maintenance)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "🔧 Maintenance & Service",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                showForm = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("➕ Add Maintenance Record")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (maintenanceList.isEmpty()) {

            Text(
                text = "No maintenance records yet.",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    maintenanceList,
                    key = { it.id }
                ) { maintenance ->

                    MaintenanceCard(
                        maintenance = maintenance,
                        onDelete = {
                            scope.launch {
                                dao.deleteMaintenance(maintenance)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    maintenance: Maintenance,
    onDelete: () -> Unit
) {

    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "🚛 ${maintenance.vehicleNo}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("🔧 Type: ${maintenance.type}")

            Text("💰 Cost: ₹${maintenance.cost}")

            if (maintenance.description.isNotBlank()) {
                Text("📝 ${maintenance.description}")
            }

            Text(
                "📅 Date: ${
                    dateFormat.format(Date(maintenance.date))
                }"
            )

            if (maintenance.nextDue > 0L) {
                Text(
                    "📆 Next Due: ${
                        dateFormat.format(Date(maintenance.nextDue))
                    }"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDelete
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
private fun MaintenanceForm(
    onSave: (Maintenance) -> Unit,
    onCancel: () -> Unit
) {

    var vehicleNo by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedType by remember {
        mutableStateOf("Service")
    }

    val types = listOf(
        "Service",
        "Tyre",
        "Oil",
        "Brake"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "➕ Add Maintenance",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = vehicleNo,
            onValueChange = {
                vehicleNo = it
            },
            label = {
                Text("Vehicle Number")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Maintenance Type")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            types.forEach { type ->

                FilterChip(
                    selected = selectedType == type,
                    onClick = {
                        selectedType = type
                    },
                    label = {
                        Text(type)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = costText,
            onValueChange = {
                costText = it.filter { char ->
                    char.isDigit()
                }
            },
            label = {
                Text("Cost ₹")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("Description")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val cost = costText.toIntOrNull() ?: 0

                if (vehicleNo.isNotBlank()) {

                    onSave(
                        Maintenance(
                            vehicleNo = vehicleNo.trim(),
                            type = selectedType,
                            cost = cost,
                            description = description.trim(),
                            date = System.currentTimeMillis(),
                            nextDue = 0L
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Maintenance")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
