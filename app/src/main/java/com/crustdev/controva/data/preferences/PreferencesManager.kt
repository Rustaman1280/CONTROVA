package com.crustdev.controva.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crustdev.controva.data.model.AppSettings
import com.crustdev.controva.data.model.ControllerType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "controva_settings")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val STICK_SENSITIVITY = floatPreferencesKey("stick_sensitivity")
        val STICK_DEADZONE = floatPreferencesKey("stick_deadzone")
        val GYRO_SENSITIVITY = floatPreferencesKey("gyro_sensitivity")
        val GYRO_ENABLED = booleanPreferencesKey("gyro_enabled")
        val STEERING_MODE_GYRO = booleanPreferencesKey("steering_mode_gyro")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val USE_UDP = booleanPreferencesKey("use_udp")
        val LAST_HOST_IP = stringPreferencesKey("last_host_ip")
        val LAST_PORT = intPreferencesKey("last_port")
        val LAST_LAYOUT = stringPreferencesKey("last_layout")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { pref ->
        AppSettings(
            accentColorHex = pref[Keys.ACCENT_COLOR] ?: "#00F0FF",
            stickSensitivity = pref[Keys.STICK_SENSITIVITY] ?: 1.0f,
            stickDeadzone = pref[Keys.STICK_DEADZONE] ?: 0.08f,
            gyroSensitivity = pref[Keys.GYRO_SENSITIVITY] ?: 1.0f,
            gyroEnabled = pref[Keys.GYRO_ENABLED] ?: true,
            steeringModeGyro = pref[Keys.STEERING_MODE_GYRO] ?: false,
            hapticFeedback = pref[Keys.HAPTIC_FEEDBACK] ?: true,
            useUdp = pref[Keys.USE_UDP] ?: false,
            lastHostIp = pref[Keys.LAST_HOST_IP] ?: "192.168.1.100",
            lastPort = pref[Keys.LAST_PORT] ?: 8080,
            lastLayout = pref[Keys.LAST_LAYOUT]?.let { name ->
                try { ControllerType.valueOf(name) } catch (_: Exception) { ControllerType.PLAYSTATION }
            } ?: ControllerType.PLAYSTATION
        )
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { pref ->
            val current = AppSettings(
                accentColorHex = pref[Keys.ACCENT_COLOR] ?: "#00F0FF",
                stickSensitivity = pref[Keys.STICK_SENSITIVITY] ?: 1.0f,
                stickDeadzone = pref[Keys.STICK_DEADZONE] ?: 0.08f,
                gyroSensitivity = pref[Keys.GYRO_SENSITIVITY] ?: 1.0f,
                gyroEnabled = pref[Keys.GYRO_ENABLED] ?: true,
                steeringModeGyro = pref[Keys.STEERING_MODE_GYRO] ?: false,
                hapticFeedback = pref[Keys.HAPTIC_FEEDBACK] ?: true,
                useUdp = pref[Keys.USE_UDP] ?: false,
                lastHostIp = pref[Keys.LAST_HOST_IP] ?: "192.168.1.100",
                lastPort = pref[Keys.LAST_PORT] ?: 8080,
                lastLayout = pref[Keys.LAST_LAYOUT]?.let { name ->
                    try { ControllerType.valueOf(name) } catch (_: Exception) { ControllerType.PLAYSTATION }
                } ?: ControllerType.PLAYSTATION
            )
            val updated = transform(current)
            pref[Keys.ACCENT_COLOR] = updated.accentColorHex
            pref[Keys.STICK_SENSITIVITY] = updated.stickSensitivity
            pref[Keys.STICK_DEADZONE] = updated.stickDeadzone
            pref[Keys.GYRO_SENSITIVITY] = updated.gyroSensitivity
            pref[Keys.GYRO_ENABLED] = updated.gyroEnabled
            pref[Keys.STEERING_MODE_GYRO] = updated.steeringModeGyro
            pref[Keys.HAPTIC_FEEDBACK] = updated.hapticFeedback
            pref[Keys.USE_UDP] = updated.useUdp
            pref[Keys.LAST_HOST_IP] = updated.lastHostIp
            pref[Keys.LAST_PORT] = updated.lastPort
            pref[Keys.LAST_LAYOUT] = updated.lastLayout.name
        }
    }
}
