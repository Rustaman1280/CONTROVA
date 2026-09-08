package com.crustdev.controva.data.network

import android.util.Log
import com.crustdev.controva.data.model.ConnectionState
import com.crustdev.controva.data.model.ControllerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class NetworkManager {

    private val tag = "NetworkManager"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // infinite for websocket
        .build()

    private var activeWebSocket: WebSocket? = null
    private var udpSocket: DatagramSocket? = null
    private var udpTargetAddress: InetAddress? = null
    private var udpTargetPort: Int = 8080

    private var heartbeatJob: Job? = null
    private var eventConsumerJob: Job? = null

    // Channel with conflation to prevent buffer bloat and guarantee lowest latency
    private val eventChannel = Channel<ControllerEvent>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        startEventConsumer()
    }

    private fun startEventConsumer() {
        eventConsumerJob?.cancel()
        eventConsumerJob = scope.launch {
            for (event in eventChannel) {
                sendImmediately(event)
            }
        }
    }

    fun connect(host: String, port: Int, useUdp: Boolean = false) {
        disconnect()
        _connectionState.value = ConnectionState.Connecting(host, port)

        if (useUdp) {
            connectUdp(host, port)
        } else {
            connectWebSocket(host, port)
        }
    }

    private fun connectWebSocket(host: String, port: Int) {
        val sanitizedHost = host.trim().removePrefix("ws://").removePrefix("http://")
        val url = "ws://$sanitizedHost:$port"

        val request = try {
            Request.Builder().url(url).build()
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error("Invalid URL: ${e.localizedMessage}")
            return
        }

        activeWebSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "WebSocket connected to $url")
                _connectionState.value = ConnectionState.Connected(host, port, pingMs = 5)
                startHeartbeat(host, port)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingMessage(text, host, port)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _connectionState.value = ConnectionState.Disconnected
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "WebSocket error", t)
                _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
                stopHeartbeat()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _connectionState.value = ConnectionState.Disconnected
                stopHeartbeat()
            }
        })
    }

    private fun connectUdp(host: String, port: Int) {
        scope.launch {
            try {
                udpTargetAddress = InetAddress.getByName(host.trim())
                udpTargetPort = port
                udpSocket = DatagramSocket()
                _connectionState.value = ConnectionState.Connected(host, port, pingMs = 1)
                startHeartbeat(host, port)
            } catch (e: Exception) {
                Log.e(tag, "UDP setup error", e)
                _connectionState.value = ConnectionState.Error("UDP Error: ${e.localizedMessage}")
            }
        }
    }

    fun sendEvent(event: ControllerEvent) {
        eventChannel.trySend(event)
    }

    private fun sendImmediately(event: ControllerEvent) {
        val json = event.toJson()
        val ws = activeWebSocket
        if (ws != null) {
            ws.send(json)
            return
        }

        val socket = udpSocket
        val address = udpTargetAddress
        if (socket != null && address != null) {
            try {
                val bytes = json.toByteArray(Charsets.UTF_8)
                val packet = DatagramPacket(bytes, bytes.size, address, udpTargetPort)
                socket.send(packet)
            } catch (e: Exception) {
                Log.e(tag, "UDP send failed", e)
            }
        }
    }

    private fun handleIncomingMessage(text: String, host: String, port: Int) {
        try {
            // Heartbeat response / pong: {"pong": 123456789}
            if (text.contains("pong")) {
                val current = _connectionState.value
                if (current is ConnectionState.Connected) {
                    val sentTime = text.filter { it.isDigit() }.toLongOrNull() ?: System.currentTimeMillis()
                    val latency = (System.currentTimeMillis() - sentTime).coerceAtLeast(1L)
                    _connectionState.value = current.copy(pingMs = latency)
                }
            }
        } catch (_: Exception) {}
    }

    private fun startHeartbeat(host: String, port: Int) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(2000)
                if (_connectionState.value is ConnectionState.Connected) {
                    val now = System.currentTimeMillis()
                    sendEvent(ControllerEvent.Heartbeat(now))
                }
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun disconnect() {
        stopHeartbeat()
        try {
            activeWebSocket?.close(1000, "User disconnected")
        } catch (_: Exception) {}
        activeWebSocket = null

        try {
            udpSocket?.close()
        } catch (_: Exception) {}
        udpSocket = null
        udpTargetAddress = null

        _connectionState.value = ConnectionState.Disconnected
    }
}
