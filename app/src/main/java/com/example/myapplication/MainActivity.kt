package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button


class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACCESSIBILITY_SERVICE_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            // Check if the accessibility service is enabled
            if (!isAccessibilityServiceEnabled()) {
                // Request the user to enable the accessibility service
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivityForResult(intent, ACCESSIBILITY_SERVICE_REQUEST_CODE)
            } else {
                // Start the com.example.myapplication.AppDetectionService
                startAppDetectionService()
            }
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            // Stop the com.example.myapplication.AppDetectionService
            stopAppDetectionService()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilitySettings = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return accessibilitySettings?.contains(packageName) == true
    }

    private fun startAppDetectionService() {
        val serviceIntent = Intent(this, AppDetectionService::class.java)
        startService(serviceIntent)
    }

    private fun stopAppDetectionService() {
        val serviceIntent = Intent(this, AppDetectionService::class.java)
        stopService(serviceIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACCESSIBILITY_SERVICE_REQUEST_CODE) {
            if (isAccessibilityServiceEnabled()) {
                // Start the com.example.myapplication.AppDetectionService if the user has enabled the accessibility service
                startAppDetectionService()
            }
        }
    }
}
