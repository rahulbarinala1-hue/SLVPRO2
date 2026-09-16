package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_logs")
data class FuelLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val vehicleNo: String = "",
    val litres: Float = 0f,
    val amount: Int = 0,
    val kmReading: Int = 0,
    val station: String = "",
    val date: Long = System.currentTimeMillis()
)
