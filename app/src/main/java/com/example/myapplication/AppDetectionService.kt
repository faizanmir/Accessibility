package com.example.myapplication

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast


class AppDetectionService : AccessibilityService() {

    companion object {
        private const val TAG = "AppDetectionService"
        const val REGISTER_LISTENERS = "com.example.myapplication.REGISTER_LISTENERS"
        const val UNREGISTER_LISTENERS = "com.example.myapplication.UNREGISTER_LISTENERS"
        const val KEY = "HARDWARE"
        const val AUDIO = "audio"
    }

    private lateinit var cameraAvailabilityCallback: CameraAvailabilityCallback
    private lateinit var audioRecordingCallback: AudioRecordingCallback
    private lateinit var cameraManager: CameraManager
    private lateinit var audioManager: AudioManager
    private var registeredCallbacks = false

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == REGISTER_LISTENERS && registeredCallbacks.not()) {
                registerCameraCallback()
                registerAudioRecordingCallback()
                registeredCallbacks = true
            } else if (intent?.action == UNREGISTER_LISTENERS && registeredCallbacks) {
                unregisterCameraAvailabilityCallback()
                unregisterAudioRecordingCallback()
                registeredCallbacks = false
            }
        }
    }

    private val intentFiler = IntentFilter().apply {
        addAction(REGISTER_LISTENERS)
        addAction(UNREGISTER_LISTENERS)
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(broadcastReceiver, intentFiler)
        initializeCameraManager()
        initializeAudioManager()
        registeredCallbacks = true
        Log.d(TAG, "onServiceConnected: Service connected, callbacks registered")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //No Impl
    }

    override fun onInterrupt() {
        //No impl
    }

    private fun initializeAudioManager() {
        if (this::audioManager.isInitialized.not()) {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            buildAudioCallback()
            registerAudioRecordingCallback()
        }
    }

    private fun registerAudioRecordingCallback() {
        audioManager.registerAudioRecordingCallback(audioRecordingCallback, null)
    }

    private fun unregisterAudioRecordingCallback() {
        audioManager.unregisterAudioRecordingCallback(audioRecordingCallback)
    }

    private fun initializeCameraManager() {
        if (this::cameraManager.isInitialized.not()) {
            cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            buildCameraCallback()
        }
    }

    private fun buildCameraCallback() {
        cameraAvailabilityCallback = CameraAvailabilityCallback { isCameraAvailable ->
            if (isCameraAvailable.not()) {
                showOverlay(false)
            }
        }
        registerCameraCallback()
    }

    private fun buildAudioCallback() {
        audioRecordingCallback = AudioRecordingCallback { isMicOn ->
            if (isMicOn) {
               showOverlay(true)
                Toast.makeText(this, "Mic usage detected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun unregisterCameraAvailabilityCallback() {
        cameraManager.unregisterAvailabilityCallback(cameraAvailabilityCallback)
    }

    private fun registerCameraCallback() {
        cameraManager.registerAvailabilityCallback(
            cameraAvailabilityCallback,
            Handler(Looper.myLooper() ?: Looper.getMainLooper())
        )
    }

    private fun showOverlay(isRecordingAudio:Boolean) {
        startActivity(Intent(
            this@AppDetectionService, MainActivity::class.java
        ).apply {
            if (isRecordingAudio) {
                putExtra(KEY, AUDIO)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
