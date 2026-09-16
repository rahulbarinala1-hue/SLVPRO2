package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey
    val driverId: String,

    val name: String = "",
    val licenseNo: String = "",
    val phone: String = "",
    val licenseExpiry: Long = 0L,
    val assignedVehicle: String = ""
)
