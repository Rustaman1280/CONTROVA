# CONTROVA — Agent Instructions & Knowledge

CONTROVA is a native Android gamepad and virtual controller application built with Kotlin and Jetpack Compose. It allows a smartphone to act as a wireless game controller / steering wheel for a PC or console companion application over local WiFi (WebSocket / raw UDP socket).

## Agent skills

### Issue tracker

GitHub issues at `https://github.com/Rustaman1280/CONTROVA`. See `docs/agents/issue-tracker.md`.

### Triage labels

Canonical roles (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`). See `docs/agents/triage-labels.md`.

### Domain docs

Single-context (`CONTEXT.md` + `docs/adr/` at repo root). See `docs/agents/domain.md`.

---

## Architectural Principles
1. **Low Latency Input Stream**: Input events are transmitted via conflated channels (`BufferOverflow.DROP_OLDEST`) over WebSocket (OkHttp) or raw UDP socket to avoid queuing delays.
2. **Modular Layout System**: Controller layouts (`PlayStation`, `Xbox`, `Nintendo`, `SteeringWheel`) share common design tokens and reusable components (`GlowButton`, `AnalogStick`, `DPad`, `TriggerButton`, `SteeringWheel`, `PedalControl`, `ConnectionStatusBadge`).
3. **Design Aesthetic**: Modern dark minimalism (`#0B0D12` background) with subtle soft blur glow (Electric Cyan `#00F0FF` / Neon Violet `#A855F7`), clean geometry, and micro-interactions.
4. **Sensor Fusion**: Gyroscope steering and aiming use `TYPE_GAME_ROTATION_VECTOR` with low-pass filtering and recenter calibration to eliminate jitter.
