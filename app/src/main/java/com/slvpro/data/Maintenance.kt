package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val vehicleNo: String = "",

    // Service / Tyre / Oil / Brake
    val type: String = "Service",

    val cost: Int = 0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val nextDue: Long = 0L
)
