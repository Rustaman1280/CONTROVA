package com.crustdev.controva.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI

data class GyroData(
    val pitch: Float = 0f, // X-axis tilt (up / down) in degrees
    val roll: Float = 0f,  // Y-axis tilt (left / right rotation) in degrees
    val yaw: Float = 0f,   // Z-axis rotation (steering azimuth) in degrees
    val normalizedWheelTurn: Float = 0f // -1.0 .. 1.0 steering calculation
)

class GyroSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Prefer GAME_ROTATION_VECTOR (no magnetic distortion)
    private val rotationSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _gyroData = MutableStateFlow(GyroData())
    val gyroData: StateFlow<GyroData> = _gyroData.asStateFlow()

    private var isListening = false

    // Calibration offsets
    private var basePitch = 0f
    private var baseRoll = 0f
    private var baseYaw = 0f

    // Low-pass filter smoothed values
    private var smoothedPitch = 0f
    private var smoothedRoll = 0f
    private var smoothedYaw = 0f

    // Smoothing factor: 0.0 = no update, 1.0 = raw. 0.3 offers great jitter reduction with minimal lag
    private val filterAlpha = 0.32f

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    fun startListening() {
        if (isListening || rotationSensor == null) return
        sensorManager.registerListener(
            this,
            rotationSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        isListening = true
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    fun recenter() {
        basePitch = smoothedPitch
        baseRoll = smoothedRoll
        baseYaw = smoothedYaw
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR ||
            event.sensor.type == Sensor.TYPE_ROTATION_VECTOR
        ) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // Convert radians to degrees
            val radToDeg = (180.0 / PI).toFloat()
            val rawYaw = orientationAngles[0] * radToDeg
            val rawPitch = orientationAngles[1] * radToDeg
            val rawRoll = orientationAngles[2] * radToDeg

            // Low-pass exponential filter
            smoothedPitch += filterAlpha * (rawPitch - smoothedPitch)
            smoothedRoll += filterAlpha * (rawRoll - smoothedRoll)
            smoothedYaw += filterAlpha * (rawYaw - smoothedYaw)

            // Apply calibration offsets
            val calibratedPitch = smoothedPitch - basePitch
            val calibratedRoll = smoothedRoll - baseRoll
            val calibratedYaw = smoothedYaw - baseYaw

            // Normalized steering wheel calculation: roll tilt in landscape
            // Clamped to -90..90 deg mapped to -1.0 .. 1.0
            val wheelTurn = (calibratedRoll / 70f).coerceIn(-1.0f, 1.0f)

            _gyroData.value = GyroData(
                pitch = calibratedPitch,
                roll = calibratedRoll,
                yaw = calibratedYaw,
                normalizedWheelTurn = wheelTurn
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
