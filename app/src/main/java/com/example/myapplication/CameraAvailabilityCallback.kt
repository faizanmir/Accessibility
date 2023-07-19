package com.example.myapplication

import android.hardware.camera2.CameraManager

class CameraAvailabilityCallback constructor(val onCameraAvailabilityChange: (available: Boolean) -> Unit) :
    CameraManager.AvailabilityCallback() {

    override fun onCameraUnavailable(cameraId: String) {
        super.onCameraUnavailable(cameraId)
        onCameraAvailabilityChange(false)
    }

    override fun onCameraAvailable(cameraId: String) {
        super.onCameraAvailable(cameraId)
        onCameraAvailabilityChange(true)
    }
}