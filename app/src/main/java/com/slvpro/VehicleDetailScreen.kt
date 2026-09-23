package com.slvpro
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slvpro.data.Vehicle
@Composable
fun VehicleDetailScreen(
    vehicle: Vehicle,
    context: Context,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(onClick = onBack) {
                Text("<- Back")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Vehicle: ${vehicle.vehicleNO}")
            }
        }
    }
}
