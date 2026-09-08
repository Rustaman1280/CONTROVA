package com.crustdev.controva.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustdev.controva.data.model.AppSettings
import com.crustdev.controva.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val settings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    fun updateStickSensitivity(value: Float) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(stickSensitivity = value) }
        }
    }

    fun updateStickDeadzone(value: Float) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(stickDeadzone = value) }
        }
    }

    fun updateGyroSensitivity(value: Float) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(gyroSensitivity = value) }
        }
    }

    fun updateAccentColor(hex: String) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(accentColorHex = hex) }
        }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(hapticFeedback = enabled) }
        }
    }

    fun updateUseUdp(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateSettings { it.copy(useUdp = enabled) }
        }
    }
}
