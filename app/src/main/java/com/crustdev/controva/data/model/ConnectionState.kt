package com.crustdev.controva.data.model

sealed interface ConnectionState {
    object Disconnected : ConnectionState
    data class Connecting(val host: String, val port: Int) : ConnectionState
    data class Connected(val host: String, val port: Int, val pingMs: Long = 0) : ConnectionState
    data class Error(val message: String) : ConnectionState

    val isConnected: Boolean
        get() = this is Connected
}
