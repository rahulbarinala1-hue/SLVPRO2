package com.slvpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val vehicleNo: String = "",

    // Toll / Police / Food / Loading / Repair
    val category: String = "Toll",

    val amount: Int = 0,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)
