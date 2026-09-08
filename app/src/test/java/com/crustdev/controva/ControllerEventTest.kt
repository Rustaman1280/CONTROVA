package com.crustdev.controva

import com.crustdev.controva.data.model.ControllerEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ControllerEventTest {

    @Test
    fun testButtonEventJsonSerialization() {
        val event = ControllerEvent.Button(id = "CROSS", pressed = true, time = 1000L)
        val json = event.toJson()

        assertTrue(json.contains("\"id\":\"CROSS\""))
        assertTrue(json.contains("\"pressed\":true"))
        assertTrue(json.contains("\"type\":\"button\""))
    }

    @Test
    fun testStickEventJsonSerialization() {
        val event = ControllerEvent.Stick(id = "left", x = 0.75f, y = -0.5f, time = 1000L)
        val json = event.toJson()

        assertTrue(json.contains("\"id\":\"left\""))
        assertTrue(json.contains("\"x\":0.75"))
        assertTrue(json.contains("\"y\":-0.5"))
        assertTrue(json.contains("\"type\":\"stick\""))
    }

    @Test
    fun testWheelEventJsonSerialization() {
        val event = ControllerEvent.Wheel(angle = 45.0f, normalizedTurn = 0.5f, time = 1000L)
        val json = event.toJson()

        assertTrue(json.contains("\"angle\":45.0"))
        assertTrue(json.contains("\"normalizedTurn\":0.5"))
        assertTrue(json.contains("\"type\":\"wheel\""))
    }

    @Test
    fun testTriggerEventJsonSerialization() {
        val event = ControllerEvent.Trigger(id = "R2", pressure = 0.88f, time = 1000L)
        val json = event.toJson()

        assertTrue(json.contains("\"id\":\"R2\""))
        assertTrue(json.contains("\"pressure\":0.88"))
        assertTrue(json.contains("\"type\":\"trigger\""))
    }

    @Test
    fun testHeartbeatEventJsonSerialization() {
        val event = ControllerEvent.Heartbeat(pingId = 123456789L, time = 1000L)
        val json = event.toJson()

        assertTrue(json.contains("\"pingId\":123456789"))
        assertTrue(json.contains("\"type\":\"heartbeat\""))
    }
}
