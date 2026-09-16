package com.slvpro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.AppDatabase
import com.slvpro.data.Expense
import kotlinx.coroutines.launch

@Composable
fun ExpenseManagementScreen(database: AppDatabase) {

    val dao = database.fleetDao()
    val scope = rememberCoroutineScope()

    val expenses by dao
        .getExpenses()
        .collectAsState(initial = emptyList())

    val totalExpense by dao
        .totalExpense()
        .collectAsState(initial = 0)

    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        ExpenseForm(
            onSave = { expense ->
                scope.launch {
                    dao.insertExpense(expense)
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
                "Expense Tracker",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    showForm = true
                }
            ) {
                Text("Add Expense")
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            Modifier.fillMaxWidth()
        ) {

            Column(
                Modifier.padding(16.dp)
            ) {

                Text(
                    "Total Expense",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "₹$totalExpense",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Expense Entries: ${expenses.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                expenses,
                key = { it.id }
            ) { expense ->

                Card(
                    Modifier.fillMaxWidth()
                ) {

                    Column(
                        Modifier.padding(16.dp)
                    ) {

                        Text(
                            expense.category,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            "Vehicle: ${expense.vehicleNo}"
                        )

                        Text(
                            "Amount: ₹${expense.amount}"
                        )

                        if (expense.note.isNotBlank()) {
                            Text(
                                "Note: ${expense.note}"
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    dao.deleteExpense(expense)
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
private fun ExpenseForm(
    onSave: (Expense) -> Unit,
    onCancel: () -> Unit
) {

    var vehicleNo by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("Toll")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var note by remember {
        mutableStateOf("")
    }

    val categories = listOf(
        "Toll",
        "Police",
        "Food",
        "Loading",
        "Repair",
        "Fuel",
        "Other"
    )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        item {
            Text(
                "Add Expense",
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
            Text(
                "Category",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                categories.take(4).forEach { item ->

                    FilterChip(
                        selected = category == item,
                        onClick = {
                            category = item
                        },
                        label = {
                            Text(item)
                        }
                    )
                }
            }
        }

        item {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                categories.drop(4).forEach { item ->

                    FilterChip(
                        selected = category == item,
                        onClick = {
                            category = item
                        },
                        label = {
                            Text(item)
                        }
                    )
                }
            }
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
                value = note,
                onValueChange = {
                    note = it
                },
                label = {
                    Text("Note")
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
                            Expense(
                                vehicleNo =
                                    vehicleNo.trim(),
                                category = category,
                                amount =
                                    amount.toIntOrNull()
                                        ?: 0,
                                note = note.trim(),
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
