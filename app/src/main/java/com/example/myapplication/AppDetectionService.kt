package com.example.myapplication

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import java.lang.Exception
import java.lang.reflect.Method


class AppDetectionService : AccessibilityService() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "AppDetectionChannel"
        private const val TAG = "AppDetectionService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        showNotification()
    }



    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            Log.e(TAG, "onAccessibilityEvent: ${event.packageName}")

            // Check if the camera application or camera-related activity is in the foreground
            if (packageName?.lowercase()?.contains("camera")== true) {
                // Camera is being used
                // Replace the following line with your desired implementation
                Log.e(TAG, "onAccessibilityEvent: Camera is being used")
//                startActivity(Intent(this, MainActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                })

                try {
                    val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                    val forceStopPackageMethod = am.javaClass.getMethod("forceStopPackage", String::class.java)
                    forceStopPackageMethod.invoke(am,packageName)
                } catch (e : Exception) {
                    Log.e(TAG, "onAccessibilityEvent: ",e )
                }
            }
        }
    }


    override fun onInterrupt() {
        // Not used in this example
    }

    private fun showNotification() {
        createNotificationChannel()

        val notificationIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Detection Service")
            .setContentText("Running...")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Detection Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lightColor = Color.GREEN
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}
