package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val lrNo: String,

    val vehicleNo: String = "",
    val driverId: String = "",

    val fromLocation: String = "",
    val toLocation: String = "",

    val freight: Int = 0,
    val advance: Int = 0,

    val startDate: Long = System.currentTimeMillis(),

    // Ongoing / Completed
    val status: String = "Ongoing"
)
