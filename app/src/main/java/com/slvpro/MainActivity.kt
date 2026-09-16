package com.slvpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.slvpro.data.AppDatabase
import com.slvpro.data.Driver
import com.slvpro.data.Expense
import com.slvpro.data.Trip
import com.slvpro.data.Vehicle
import com.slvpro.worker.ExpiryWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(this)

        insertSampleDataIfNeeded(database)

        ExpiryWorker.scheduleExpiryCheck(this)

        setContent {
            MaterialTheme {
                Surface {
                    SLVProFullApp()
                }
            }
        }
    }

    private fun insertSampleDataIfNeeded(
        database: AppDatabase
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val dao = database.fleetDao()

            if (dao.vehicleCount() == 0) {

                val now = System.currentTimeMillis()

                dao.insertVehicle(
                    Vehicle(
                        vehicleNo = "KA51 AB 1234",
                        type = "Truck",
                        model = "Tata 5530",
                        owner = "SLV Trans",
                        chassisNo = "CHASSIS001",
                        pucExpiry = now + TimeUnit.DAYS.toMillis(3),
                        insuranceExpiry = now + TimeUnit.DAYS.toMillis(7),
                        fitnessExpiry = now + TimeUnit.DAYS.toMillis(15),
                        permitType = "National",
                        permitExpiry = now + TimeUnit.DAYS.toMillis(30),
                        taxExpiry = now + TimeUnit.DAYS.toMillis(60),
                        fastagBalance = 450,
                        fastagId = "TAG123"
                    )
                )

                dao.insertVehicle(
                    Vehicle(
                        vehicleNo = "KA01 CD 5678",
                        type = "Lorry",
                        model = "Ashok Leyland 3118",
                        owner = "SLV Trans",
                        chassisNo = "CHASSIS002",
                        pucExpiry = now + TimeUnit.DAYS.toMillis(1),
                        insuranceExpiry = now + TimeUnit.DAYS.toMillis(20),
                        fitnessExpiry = now + TimeUnit.DAYS.toMillis(30),
                        permitType = "State",
                        permitExpiry = now + TimeUnit.DAYS.toMillis(45),
                        taxExpiry = now + TimeUnit.DAYS.toMillis(60),
                        fastagBalance = 1200,
                        fastagId = "TAG456"
                    )
                )

                dao.insertDriver(
                    Driver(
                        driverId = "D001",
                        name = "Ramesh",
                        licenseNo = "DL001",
                        phone = "",
                        licenseExpiry = now + TimeUnit.DAYS.toMillis(365),
                        assignedVehicle = "KA51 AB 1234"
                    )
                )

                dao.insertTrip(
                    Trip(
                        lrNo = "LR001",
                        vehicleNo = "KA51 AB 1234",
                        driverId = "D001",
                        fromLocation = "Bangalore",
                        toLocation = "Mumbai",
                        freight = 25000,
                        advance = 0,
                        startDate = now,
                        status = "Ongoing"
                    )
                )

                dao.insertExpense(
                    Expense(
                        vehicleNo = "KA51 AB 1234",
                        category = "Toll",
                        amount = 1500,
                        note = "Sample toll expense",
                        date = now
                    )
                )

                dao.insertExpense(
                    Expense(
                        vehicleNo = "KA51 AB 1234",
                        category = "Fuel",
                        amount = 5000,
                        note = "Sample fuel expense",
                        date = now
                    )
                )
            }
        }
    }
}
