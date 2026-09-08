package com.crustdev.controva.presentation.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustdev.controva.data.model.AppSettings
import com.crustdev.controva.data.model.ConnectionState
import com.crustdev.controva.data.model.ControllerEvent
import com.crustdev.controva.data.model.ControllerType
import com.crustdev.controva.data.network.NetworkManager
import com.crustdev.controva.data.preferences.PreferencesManager
import com.crustdev.controva.data.sensor.GyroData
import com.crustdev.controva.data.sensor.GyroSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ControllerViewModel(
    private val networkManager: NetworkManager,
    private val gyroSensorManager: GyroSensorManager,
    private val preferencesManager: PreferencesManager,
    initialLayout: ControllerType
) : ViewModel() {

    private val _layout = MutableStateFlow(initialLayout)
    val layout: StateFlow<ControllerType> = _layout.asStateFlow()

    val connectionState: StateFlow<ConnectionState> = networkManager.connectionState
    val gyroData: StateFlow<GyroData> = gyroSensorManager.gyroData

    val settings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private val _isGyroEnabled = MutableStateFlow(true)
    val isGyroEnabled: StateFlow<Boolean> = _isGyroEnabled.asStateFlow()

    init {
        gyroSensorManager.startListening()

        // Gyro input event streaming loop
        viewModelScope.launch {
            gyroSensorManager.gyroData.collect { g ->
                if (_isGyroEnabled.value) {
                    val sens = settings.value.gyroSensitivity
                    // Stream gyro aim event if active
                    networkManager.sendEvent(
                        ControllerEvent.Gyro(
                            pitch = g.pitch * sens,
                            roll = g.roll * sens,
                            yaw = g.yaw * sens
                        )
                    )
                }
            }
        }
    }

    fun setLayout(newLayout: ControllerType) {
        _layout.value = newLayout
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(lastLayout = newLayout) }
        }
    }

    fun toggleGyro() {
        val next = !_isGyroEnabled.value
        _isGyroEnabled.value = next
    }

    fun recenterGyro() {
        gyroSensorManager.recenter()
    }

    fun sendEvent(event: ControllerEvent) {
        networkManager.sendEvent(event)
    }

    override fun onCleared() {
        super.onCleared()
        gyroSensorManager.stopListening()
    }
}
