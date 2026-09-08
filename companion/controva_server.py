#!/usr/bin/env python3
"""
CONTROVA Host Companion Test Server
Lightweight Python server to receive and verify real-time gamepad input events
from the CONTROVA Android application via WebSocket and UDP.

Usage:
    python controva_server.py [--port 8080] [--udp-port 8080]
"""

import argparse
import asyncio
import json
import socket
import sys
import threading
import time

try:
    import websockets
except ImportError:
    websockets = None
    print("[WARNING] 'websockets' library not installed. WebSocket server will not run.")
    print("           Run 'pip install websockets' to enable WebSocket support.")


def format_event(data: dict) -> str:
    ev_type = data.get("type", "unknown")
    if ev_type == "button":
        btn = data.get("id", "?")
        pressed = "PRESSED" if data.get("pressed") else "RELEASED"
        return f"[BUTTON] {btn:12} -> {pressed}"
    elif ev_type == "stick":
        sid = data.get("id", "?")
        x = data.get("x", 0.0)
        y = data.get("y", 0.0)
        return f"[STICK]  {sid:12} -> X: {x:+.2f}  Y: {y:+.2f}"
    elif ev_type == "trigger":
        tid = data.get("id", "?")
        p = data.get("pressure", 0.0)
        return f"[TRIGGER]{tid:12} -> Pressure: {p:.2f}"
    elif ev_type == "gyro":
        pitch = data.get("pitch", 0.0)
        roll = data.get("roll", 0.0)
        yaw = data.get("yaw", 0.0)
        return f"[GYRO]   Pitch: {pitch:+06.1f}° | Roll: {roll:+06.1f}° | Yaw: {yaw:+06.1f}°"
    elif ev_type == "wheel":
        deg = data.get("angle", 0.0)
        turn = data.get("normalizedTurn", 0.0)
        return f"[WHEEL]  Angle: {deg:+06.1f}° | Turn: {turn:+.2f}"
    elif ev_type == "heartbeat":
        return None  # Filter heartbeat from console noise
    return f"[RAW]    {json.dumps(data)}"


# --- WebSocket Handler ---
async def ws_handler(websocket):
    client_ip = websocket.remote_address[0]
    print(f"\n[+] CONTROVA Client connected via WebSocket from {client_ip}")
    try:
        async for message in websocket:
            try:
                data = json.loads(message)
                if data.get("type") == "heartbeat":
                    ping_id = data.get("pingId", int(time.time() * 1000))
                    await websocket.send(json.dumps({"pong": ping_id}))
                    continue

                formatted = format_event(data)
                if formatted:
                    print(formatted)
            except json.JSONDecodeError:
                pass
    except Exception as e:
        print(f"[-] Client disconnected: {e}")


async def run_websocket_server(host, port):
    if not websockets:
        return
    print(f"[*] WebSocket server listening on ws://{host}:{port}")
    async with websockets.serve(ws_handler, host, port):
        await asyncio.Future()  # run forever


# --- UDP Server Handler ---
def run_udp_server(host, port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((host, port))
    print(f"[*] Raw UDP server listening on udp://{host}:{port}")
    while True:
        try:
            data, addr = sock.recvfrom(2048)
            text = data.decode("utf-8", errors="ignore")
            payload = json.loads(text)
            if payload.get("type") == "heartbeat":
                continue
            formatted = format_event(payload)
            if formatted:
                print(f"[UDP] {formatted}")
        except Exception:
            pass


def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
    except Exception:
        ip = "127.0.0.1"
    finally:
        s.close()
    return ip


def main():
    parser = argparse.ArgumentParser(description="CONTROVA PC Companion Server")
    parser.add_argument("--port", type=int, default=8080, help="Port for WebSocket & UDP (default: 8080)")
    args = parser.parse_args()

    local_ip = get_local_ip()
    print("=" * 60)
    print("           CONTROVA COMPANION HOST SERVER")
    print("=" * 60)
    print(f"Local IP Address : {local_ip}")
    print(f"Target Port      : {args.port}")
    print(f"Enter this IP in your CONTROVA Android App -> {local_ip}:{args.port}")
    print("=" * 60)

    # Start UDP listener thread
    udp_thread = threading.Thread(target=run_udp_server, args=("0.0.0.0", args.port), daemon=True)
    udp_thread.start()

    # Start WebSocket listener
    if websockets:
        try:
            asyncio.run(run_websocket_server("0.0.0.0", args.port))
        except KeyboardInterrupt:
            print("\n[!] Shutting down server.")
    else:
        print("[*] Running UDP only mode. Press Ctrl+C to stop.")
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            print("\n[!] Shutting down server.")


if __name__ == "__main__":
    main()
