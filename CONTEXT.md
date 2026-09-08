# CONTROVA Domain Context

## Overview
CONTROVA is a native Android virtual gamepad application built with Kotlin and Jetpack Compose. It turns a mobile device into a high-performance wireless controller for PCs and gaming hosts over local WiFi.

## Domain Concepts

### Controller Layouts
- **PlayStation Layout**: Symmetrical thumbsticks, directional pad, symbol buttons (△, ○, ✕, □), shoulder buttons (L1/R1), triggers (L2/R2), Share and Options.
- **Xbox Layout**: Asymmetrical thumbsticks (left-top, right-bottom), D-pad, face buttons (A, B, X, Y), bumpers (LB/RB), triggers (LT/RT), View and Menu.
- **Nintendo Layout**: Diamond face buttons (X top, B bottom, Y left, A right), dual sticks, L/R/ZL/ZR bumpers and triggers, Plus (+) and Minus (-) buttons.
- **Steering Wheel Layout**: Racing cockpit mode featuring a large virtual steering wheel (switchable between touch drag and phone gyroscope tilt), vertical Brake and Gas pedals, paddle shifters, gear indicator, and recenter controls.

### Input Events
- `ButtonEvent`: Identifies key down/up states for digital buttons.
- `StickEvent`: Normalized $(-1.0 \dots 1.0)$ coordinate pairs for left and right analog sticks with circular deadzone handling.
- `TriggerEvent`: Analog pressure level ($0.0 \dots 1.0$) for progressive trigger pulls.
- `GyroEvent`: Rotational motion orientation (pitch, roll, yaw) derived from Android sensor fusion.
- `WheelEvent`: Steering rotation angle in degrees ($-180^\circ \dots 180^\circ$) and normalized steering ratio ($-1.0 \dots 1.0$).
- `HeartbeatEvent`: Periodic latency check ping/pong.

### Connection & Session
- `ConnectionState`: State machine covering `Disconnected`, `Connecting`, `Connected`, and `Error`.
- `NetworkTransport`: Transports input payloads over WebSocket (OkHttp) or UDP socket with flow conflation.
- `LatencyMonitor`: Measures round-trip time (RTT) displayed on the top status pill badge.

### Gyroscope & Sensor Fusion
- Uses `Sensor.TYPE_GAME_ROTATION_VECTOR` (or fallback `TYPE_ROTATION_VECTOR`) to eliminate compass drift.
- Exponential moving average low-pass filter to reject hand tremor jitter.
- Recenter offset matrix to set current holding angle as zero.

### Design Tokens & Aesthetics
- Background: `#0B0D12` (near-black).
- Accent Colors: Electric Cyan `#00F0FF`, Neon Violet `#A855F7`, Emerald `#10B981`, Crimson `#EF4444`.
- Soft Glow: Soft blurred shadows via Compose drawing layer with subtle spread and low opacity.
