# ADR 0001: Architecture, Jetpack Compose UI, and Low-Latency Event Streaming

## Status
Accepted

## Context
CONTROVA requires ultra-low latency input transmission (sub-10ms target over local WiFi), dynamic multi-layout rendering with micro-interactions, responsive gyroscope motion capture, and a clean, maintainable modular codebase.

## Decision
1. **Presentation**: Jetpack Compose for declarative layout rendering, hardware-accelerated custom canvas drawing for analog sticks, steering wheels, and low-overhead vector rendering.
2. **State Management**: MVVM with Kotlin Coroutines and `StateFlow`/`SharedFlow`.
3. **Data Layer**:
   - WebSocket (OkHttp) as primary bi-directional transport with automatic reconnection and heartbeat ping calculation.
   - Raw UDP Socket as low-overhead alternative for latency-critical network conditions.
   - Channel conflation (`BufferOverflow.DROP_OLDEST`) to prevent buffer bloat.
   - Jetpack DataStore Preferences for user settings persistence.
4. **Sensor Fusion**: `Sensor.TYPE_GAME_ROTATION_VECTOR` combined with an exponential low-pass filter and zero-recenter quaternion calibration.

## Consequences
- Clean separation of concerns between UI components, input models, and network transport.
- Easy extensibility for new controller layouts or custom button mappings.
- Zero frame drops due to asynchronous non-blocking event dispatch.
