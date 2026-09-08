package com.crustdev.controva.data.model

import com.google.gson.Gson

sealed class ControllerEvent(
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    data class Button(
        val id: String,
        val pressed: Boolean,
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("button", time)

    data class Stick(
        val id: String, // "left" or "right"
        val x: Float,   // -1.0f .. 1.0f
        val y: Float,   // -1.0f .. 1.0f
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("stick", time)

    data class Trigger(
        val id: String, // "L2", "R2", "LT", "RT", "ZL", "ZR"
        val pressure: Float, // 0.0f .. 1.0f
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("trigger", time)

    data class Gyro(
        val pitch: Float,
        val roll: Float,
        val yaw: Float,
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("gyro", time)

    data class Wheel(
        val angle: Float,           // -180.0 .. 180.0 deg
        val normalizedTurn: Float,  // -1.0 .. 1.0
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("wheel", time)

    data class Heartbeat(
        val pingId: Long,
        val time: Long = System.currentTimeMillis()
    ) : ControllerEvent("heartbeat", time)

    fun toJson(gson: Gson = defaultGson): String = gson.toJson(this)

    companion object {
        val defaultGson = Gson()
    }
}
