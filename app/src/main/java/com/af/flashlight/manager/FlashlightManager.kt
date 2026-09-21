package com.af.flashlight.manager

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashlightManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cameraManager: CameraManager? =
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var cameraId: String? = null
    private var strobeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    var isTorchOn: Boolean = false
        private set

    val isFlashAvailable: Boolean by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private var externalTorchCallback: CameraManager.TorchCallback? = null

    init {
        findCameraWithFlash()
    }

    private fun findCameraWithFlash() {
        if (!isFlashAvailable || cameraManager == null) return
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    break
                }
            }
            if (cameraId == null && cameraManager.cameraIdList.isNotEmpty()) {
                cameraId = cameraManager.cameraIdList[0]
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding camera with flash: ${e.message}")
        }
    }

    fun turnOn() {
        stopBlinkingJob()
        setTorchEnabled(true)
    }

    fun turnOff() {
        stopBlinkingJob()
        setTorchEnabled(false)
    }

    fun startSos() {
        stopBlinkingJob()
        strobeJob = scope.launch {
            try {
                while (isActive) {
                    // S: 3 short pulses (200ms ON, 200ms OFF)
                    repeat(3) {
                        if (!isActive) return@launch
                        setTorchEnabled(true)
                        delay(200)
                        setTorchEnabled(false)
                        delay(200)
                    }

                    delay(400) // Pause between letters

                    // O: 3 long pulses (600ms ON, 200ms OFF)
                    repeat(3) {
                        if (!isActive) return@launch
                        setTorchEnabled(true)
                        delay(600)
                        setTorchEnabled(false)
                        delay(200)
                    }

                    delay(400) // Pause between letters

                    // S: 3 short pulses (200ms ON, 200ms OFF)
                    repeat(3) {
                        if (!isActive) return@launch
                        setTorchEnabled(true)
                        delay(200)
                        setTorchEnabled(false)
                        delay(200)
                    }

                    delay(1500) // Pause before repeat SOS sequence
                }
            } finally {
                setTorchEnabled(false)
            }
        }
    }

    fun startDjStrobe() {
        stopBlinkingJob()
        strobeJob = scope.launch {
            try {
                while (isActive) {
                    setTorchEnabled(true)
                    delay(80)
                    setTorchEnabled(false)
                    delay(80)
                }
            } finally {
                setTorchEnabled(false)
            }
        }
    }

    private fun stopBlinkingJob() {
        strobeJob?.cancel()
        strobeJob = null
    }

    private fun setTorchEnabled(enabled: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager?.setTorchMode(id, enabled)
            isTorchOn = enabled
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set torch mode ($enabled): ${e.message}")
        }
    }

    fun registerTorchCallback(onTorchStateChanged: (Boolean) -> Unit) {
        if (cameraManager == null) return
        externalTorchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(id: String, enabled: Boolean) {
                if (id == cameraId) {
                    isTorchOn = enabled
                    Handler(Looper.getMainLooper()).post {
                        onTorchStateChanged(enabled)
                    }
                }
            }

            override fun onTorchModeUnavailable(id: String) {
                if (id == cameraId) {
                    isTorchOn = false
                    Handler(Looper.getMainLooper()).post {
                        onTorchStateChanged(false)
                    }
                }
            }
        }
        try {
            cameraManager.registerTorchCallback(externalTorchCallback!!, Handler(Looper.getMainLooper()))
        } catch (e: Exception) {
            Log.e(TAG, "Error registering torch callback: ${e.message}")
        }
    }

    fun unregisterTorchCallback() {
        externalTorchCallback?.let {
            try {
                cameraManager?.unregisterTorchCallback(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering torch callback: ${e.message}")
            }
        }
        externalTorchCallback = null
    }

    fun release() {
        stopBlinkingJob()
        setTorchEnabled(false)
        unregisterTorchCallback()
    }

    companion object {
        private const val TAG = "FlashlightManager"
    }
}
