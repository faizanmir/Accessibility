package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        updateUi()
    }

    private fun updateUi() {
        if (intent != null) {
            findViewById<TextView>(R.id.hardwareTextView).text =
                if (intent.getStringExtra(AppDetectionService.KEY) == AppDetectionService.AUDIO)
                    "Audio recording not allowed"
                else "Camera disabled"
        }
    }


    override fun onResume() {
        super.onResume()
        sendBroadcast(Intent(AppDetectionService.REGISTER_LISTENERS))
    }

    override fun onPause() {
        super.onPause()
        sendBroadcast(Intent(AppDetectionService.REGISTER_LISTENERS))
    }


}
