package com.slvpro

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.slvpro.data.Vehicle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
) {
    OutlinedButton(
        onClick = onBack
    ) {
        Text("← Back")
    }
}
@Composable
fun VehicleDetailScreen(
    vehicle: Vehicle,
    context: Context,
    onBack: () -> Unit
) {
    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    var autoRotate by remember {
        mutableStateOf(true)
    }

    val sides = listOf(
        "Front",
        "Right",
        "Back",
        "Left"
    )

    /*
     * 360° VIEWER
     *
     * Latest requested timing:
     * Front -> Right -> Back -> Left
     * Every 10 seconds.
     */
    LaunchedEffect(autoRotate) {

        if (autoRotate) {

            while (true) {

                delay(10_000)

                currentIndex =
                    (currentIndex + 1) % sides.size
            }
        }
    }

    val imageNames = listOf(
        vehicle.frontImage,
        vehicle.rightImage,
        vehicle.backImage,
        vehicle.leftImage
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        // =========================================================
        // FIRST ITEM — 360° VIEWER
        // =========================================================

        item {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = vehicle.vehicleNo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F172A)
                    )
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        VehicleViewerImage(
                            context = context,
                            imageName = imageNames[currentIndex]
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = sides[currentIndex],
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            LinearProgressIndicator(
                                progress = {
                                    (currentIndex + 1).toFloat() /
                                            sides.size.toFloat()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Manual side selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    sides.forEachIndexed { index, side ->

                        FilterChip(
                            selected = currentIndex == index,
                            onClick = {
                                currentIndex = index
                            },
                            label = {
                                Text(side)
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Auto Rotate"
                    )

                    Switch(
                        checked = autoRotate,
                        onCheckedChange = {
                            autoRotate = it
                        }
                    )
                }
            }
        }

        // =========================================================
        // DOCUMENTS
        // =========================================================

        item {

            Text(
                text = "Vehicle Documents",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                )
            )
        }

        item {

            DocRow(
                name = "PUC",
                expiry = vehicle.pucExpiry
            )
        }

        item {

            DocRow(
                name = "Insurance",
                expiry = vehicle.insuranceExpiry
            )
        }

        item {

            DocRow(
                name = "Fitness Certificate",
                expiry = vehicle.fitnessExpiry
            )
        }

        item {

            DocRow(
                name = "${vehicle.permitType} Permit",
                expiry = vehicle.permitExpiry
            )
        }

        item {

            DocRow(
                name = "Road Tax",
                expiry = vehicle.taxExpiry
            )
        }

        // =========================================================
        // FASTAG
        // =========================================================

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "FASTag",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "FASTag ID: ${vehicle.fastagId}"
                    )

                    Text(
                        text = "Balance: ₹${vehicle.fastagBalance}"
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    if (vehicle.fastagBalance < 500) {

                        Text(
                            text = "⚠️ LOW - Recharge!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )

                    } else {

                        Text(
                            text = "✅ OK"
                        )
                    }
                }
            }
        }

        // =========================================================
        // VEHICLE INFORMATION
        // =========================================================

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp
                    )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Vehicle Information",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text("Vehicle No: ${vehicle.vehicleNo}")
                    Text("Type: ${vehicle.type}")
                    Text("Model: ${vehicle.model}")
                    Text("Owner: ${vehicle.owner}")
                    Text("Chassis No: ${vehicle.chassisNo}")
                }
            }
        }
    }
}


@Composable
private fun VehicleViewerImage(
    context: Context,
    imageName: String
) {

    val resourceId = remember(imageName) {

        context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }

    if (resourceId != 0) {

        androidx.compose.foundation.Image(
            painter = painterResource(id = resourceId),
            contentDescription = imageName,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentScale = ContentScale.Fit
        )

    } else {

        // Placeholder until you upload the real vehicle photos.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🚛",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = imageName,
                color = Color.White
            )

            Text(
                text = "Vehicle image placeholder",
                color = Color.LightGray
            )
        }
    }
}


@Composable
fun DocRow(
    name: String,
    expiry: Long
) {

    val daysLeft = calculateDaysLeft(expiry)

    val statusColor = when {

        expiry <= 0L ->
            MaterialTheme.colorScheme.onSurface

        daysLeft < 0 ->
            Color.Red

        daysLeft < 7 ->
            Color(0xFFFF9800)

        else ->
            Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 5.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = if (expiry > 0L) {
                        "Expiry: ${formatDate(expiry)}"
                    } else {
                        "Expiry date not set"
                    }
                )
            }

            Text(
                text = when {

                    expiry <= 0L ->
                        "Not set"

                    daysLeft < 0 ->
                        "Expired ${-daysLeft} days"

                    daysLeft == 0 ->
                        "Expires today"

                    else ->
                        "$daysLeft days left"
                },
                color = statusColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


private fun calculateDaysLeft(
    expiry: Long
): Int {

    if (expiry <= 0L) return 0

    val difference =
        expiry - System.currentTimeMillis()

    return ceil(
        difference.toDouble() /
                (24L * 60L * 60L * 1000L)
    ).toInt()
}


private fun formatDate(
    millis: Long
): String {

    return SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(Date(millis))
}
