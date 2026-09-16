package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase

@Composable
fun ReportsScreen(database: AppDatabase) {

    val dao = database.fleetDao()

    val vehicles by dao
        .getVehicles()
        .collectAsState(initial = emptyList())

    val totalExpense by dao
        .totalExpense()
        .collectAsState(initial = 0)

    val totalFreight by dao
        .totalFreight()
        .collectAsState(initial = 0)

    val profit = totalFreight - totalExpense

    val sevenDays =
        System.currentTimeMillis() +
            7L * 24L * 60L * 60L * 1000L

    val expiringCount = vehicles.count { vehicle ->

        listOf(
            vehicle.pucExpiry,
            vehicle.insuranceExpiry,
            vehicle.fitnessExpiry,
            vehicle.permitExpiry
        ).any { expiry ->

            expiry > 0L && expiry <= sevenDays
        }
    }

    val lowFastagCount = vehicles.count {
        it.fastagBalance < 500
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                "📊 Reports & Analytics",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            ReportCard(
                title = "🚛 Total Vehicles",
                value = vehicles.size.toString()
            )
        }

        item {
            ReportCard(
                title = "⚠️ Expiring Within 7 Days",
                value = expiringCount.toString()
            )
        }

        item {
            ReportCard(
                title = "💳 FASTag Low",
                value = lowFastagCount.toString()
            )
        }

        item {
            ReportCard(
                title = "💰 Total Expense",
                value = "₹$totalExpense"
            )
        }

        item {
            ReportCard(
                title = "📦 Total Freight",
                value = "₹$totalFreight"
            )
        }

        item {
            ReportCard(
                title = "📈 Profit",
                value = "₹$profit"
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    Modifier.padding(16.dp)
                ) {

                    Text(
                        "Financial Summary",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Total Freight: ₹$totalFreight"
                    )

                    Text(
                        "Total Expense: ₹$totalExpense"
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Profit: ₹$profit",
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                title,
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                value,
                style =
                    MaterialTheme.typography.headlineSmall
            )
        }
    }
}
