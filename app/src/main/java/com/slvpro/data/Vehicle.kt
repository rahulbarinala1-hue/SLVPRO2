package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey
    val vehicleNo: String,

    val type: String = "",
    val model: String = "",
    val owner: String = "SLV Trans",
    val chassisNo: String = "",

    // Document expiry dates - stored as milliseconds
    val pucExpiry: Long = 0L,
    val insuranceExpiry: Long = 0L,
    val fitnessExpiry: Long = 0L,
    val permitType: String = "National",
    val permitExpiry: Long = 0L,
    val taxExpiry: Long = 0L,

    // FASTag
    val fastagBalance: Int = 0,
    val fastagId: String = "",

    // Four vehicle-viewer image paths
    val frontImage: String = "vehicle_front",
    val rightImage: String = "vehicle_right",
    val backImage: String = "vehicle_back",
    val leftImage: String = "vehicle_left"
)
