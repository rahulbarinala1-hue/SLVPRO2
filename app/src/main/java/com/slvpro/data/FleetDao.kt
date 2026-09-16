package com.slvpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FleetDao {

    // ---------------- VEHICLES ----------------

    @Query("SELECT * FROM vehicles ORDER BY vehicleNo")
    fun getVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE vehicleNo = :vehicleNo LIMIT 1")
    fun getVehicle(vehicleNo: String): Flow<Vehicle?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun vehicleCount(): Int

    // ---------------- DRIVERS ----------------

    @Query("SELECT * FROM drivers ORDER BY name")
    fun getDrivers(): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: Driver)

    @Update
    suspend fun updateDriver(driver: Driver)

    @Delete
    suspend fun deleteDriver(driver: Driver)

    // ---------------- TRIPS / LR ----------------

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getTrips(): Flow<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT COALESCE(SUM(freight), 0) FROM trips")
    fun totalFreight(): Flow<Int>

    // ---------------- FUEL ----------------

    @Query("SELECT * FROM fuel_logs ORDER BY date DESC")
    fun getFuelLogs(): Flow<List<FuelLog>>

    @Query("SELECT * FROM fuel_logs WHERE vehicleNo = :vehicleNo ORDER BY date DESC")
    fun getFuelForVehicle(vehicleNo: String): Flow<List<FuelLog>>

    @Insert
    suspend fun insertFuelLog(fuelLog: FuelLog)

    @Delete
    suspend fun deleteFuelLog(fuelLog: FuelLog)

    // ---------------- MAINTENANCE ----------------

    @Query("SELECT * FROM maintenance ORDER BY date DESC")
    fun getMaintenance(): Flow<List<Maintenance>>

    @Query("SELECT * FROM maintenance WHERE vehicleNo = :vehicleNo ORDER BY date DESC")
    fun getMaintenanceForVehicle(vehicleNo: String): Flow<List<Maintenance>>

    @Insert
    suspend fun insertMaintenance(maintenance: Maintenance)

    @Update
    suspend fun updateMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)

    // ---------------- EXPENSES ----------------

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE vehicleNo = :vehicleNo ORDER BY date DESC")
    fun getExpensesForVehicle(vehicleNo: String): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    fun totalExpense(): Flow<Int>

    // ---------------- REPORTS ----------------

    @Query("""
        SELECT COUNT(*) FROM vehicles
        WHERE pucExpiry > 0
        AND pucExpiry <= :limit
    """)
    fun expiringPucCount(limit: Long): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM vehicles
        WHERE fastagBalance < 500
    """)
    fun lowFastagCount(): Flow<Int>

    // ---------------- WORKER ALERTS ----------------

    @Query("""
        SELECT * FROM vehicles
        WHERE
            (pucExpiry > 0 AND pucExpiry <= :limit)
            OR
            (insuranceExpiry > 0 AND insuranceExpiry <= :limit)
            OR
            (fitnessExpiry > 0 AND fitnessExpiry <= :limit)
            OR
            (permitExpiry > 0 AND permitExpiry <= :limit)
    """)
    suspend fun getExpiringVehicles(limit: Long): List<Vehicle>

    @Query("""
        SELECT * FROM vehicles
        WHERE fastagBalance < 500
    """)
    suspend fun getLowFastagVehicles(): List<Vehicle>
}
