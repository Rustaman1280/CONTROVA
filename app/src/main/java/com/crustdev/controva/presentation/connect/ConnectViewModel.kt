package com.crustdev.controva.presentation.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crustdev.controva.data.model.AppSettings
import com.crustdev.controva.data.model.ConnectionState
import com.crustdev.controva.data.network.NetworkManager
import com.crustdev.controva.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConnectViewModel(
    private val networkManager: NetworkManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = networkManager.connectionState

    val settings: StateFlow<AppSettings> = preferencesManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private val _hostIp = MutableStateFlow("192.168.1.100")
    val hostIp: StateFlow<String> = _hostIp.asStateFlow()

    private val _port = MutableStateFlow("8080")
    val port: StateFlow<String> = _port.asStateFlow()

    private val _useUdp = MutableStateFlow(false)
    val useUdp: StateFlow<String> = _port.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.settingsFlow.collect { s ->
                _hostIp.value = s.lastHostIp
                _port.value = s.lastPort.toString()
                _useUdp.value = s.useUdp
            }
        }
    }

    fun onHostIpChanged(newIp: String) {
        _hostIp.value = newIp
    }

    fun onPortChanged(newPort: String) {
        _port.value = newPort
    }

    fun onToggleUdp(enabled: Boolean) {
        _useUdp.value = enabled
    }

    fun connect() {
        val host = _hostIp.value.trim()
        val portInt = _port.value.toIntOrNull() ?: 8080
        val udp = _useUdp.value

        viewModelScope.launch {
            preferencesManager.updateSettings {
                it.copy(lastHostIp = host, lastPort = portInt, useUdp = udp)
            }
            networkManager.connect(host, portInt, udp)
        }
    }

    fun disconnect() {
        networkManager.disconnect()
    }
}
