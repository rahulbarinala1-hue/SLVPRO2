package com.slvpro.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.slvpro.R
import com.slvpro.data.AppDatabase
import com.slvpro.data.Vehicle
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class ExpiryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val dao = database.fleetDao()

        val now = System.currentTimeMillis()

        // Check the next 7 days.
        val sevenDays = now + TimeUnit.DAYS.toMillis(7)

        val expiringVehicles = dao.getExpiringVehicles(sevenDays)
        val lowFastagVehicles = dao.getLowFastagVehicles()

        createNotificationChannel()

        expiringVehicles.forEach { vehicle ->
            sendExpiryNotifications(vehicle, now)
        }

        lowFastagVehicles.forEach { vehicle ->
            sendNotification(
                title = "⚠️ FASTag LOW",
                message = "${vehicle.vehicleNo} - Balance ₹${vehicle.fastagBalance}. Recharge!"
            )
        }

        return Result.success()
    }

    private fun sendExpiryNotifications(
        vehicle: Vehicle,
        now: Long
    ) {
        checkExpiry(
            vehicle.vehicleNo,
            "PUC",
            vehicle.pucExpiry,
            now
        )

        checkExpiry(
            vehicle.vehicleNo,
            "Insurance",
            vehicle.insuranceExpiry,
            now
        )

        checkExpiry(
            vehicle.vehicleNo,
            "Fitness Certificate",
            vehicle.fitnessExpiry,
            now
        )

        checkExpiry(
            vehicle.vehicleNo,
            "Permit",
            vehicle.permitExpiry,
            now
        )
    }

    private fun checkExpiry(
        vehicleNo: String,
        document: String,
        expiryDate: Long,
        now: Long
    ) {
        if (expiryDate <= 0L) return

        val difference = expiryDate - now

        val daysLeft = ceil(
            difference.toDouble() / TimeUnit.DAYS.toMillis(1)
        ).toInt()

        // 7 / 3 / 1 day warning and expired warning.
        if (daysLeft <= 7) {
            val message = when {
                daysLeft < 0 ->
                    "$vehicleNo - $document expired ${-daysLeft} days ago"

                daysLeft == 0 ->
                    "$vehicleNo - $document expires today"

                else ->
                    "$vehicleNo - $document expires in $daysLeft days"
            }

            sendNotification(
                title = "🚨 $document Expiry Alert",
                message = message
            )
        }
    }

    private fun createNotificationChannel() {
        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "SLV-PRO Expiry Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Vehicle document and FASTag alerts"
        }

        manager.createNotificationChannel(channel)
    }

    private fun sendNotification(
        title: String,
        message: String
    ) {
        // Android 13+ requires notification permission.
        if (
            android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        manager.notify(
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            notification
        )
    }

    companion object {

        private const val CHANNEL_ID = "slv_pro_expiry_alerts"
        private const val WORK_NAME = "SLV_PRO_EXPIRY_CHECK"

        fun scheduleExpiryCheck(context: Context) {

            val request =
                PeriodicWorkRequestBuilder<ExpiryWorker>(
                    1,
                    TimeUnit.DAYS
                ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
