package com.example.myapplication

import android.media.AudioManager
import android.media.AudioRecordingConfiguration

class AudioRecordingCallback(val onMicStatusChange:(isMicOn:Boolean)->Unit)
    : AudioManager.AudioRecordingCallback() {

    override fun onRecordingConfigChanged(configs: MutableList<AudioRecordingConfiguration>?) {
       val isMicOn = try {
            configs?.get(0) != null;
        }catch (e:Exception) {
            false;
        }
        onMicStatusChange(isMicOn)
        super.onRecordingConfigChanged(configs)
    }

}