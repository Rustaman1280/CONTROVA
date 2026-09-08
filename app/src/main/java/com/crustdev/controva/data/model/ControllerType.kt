package com.crustdev.controva.data.model

enum class ControllerType(val displayName: String, val subtitle: String) {
    PLAYSTATION("PlayStation", "Dual symmetrical sticks, D-pad, and △○✕□ action keys"),
    XBOX("Xbox", "Asymmetric sticks, ergonomic D-pad, and ABXY diamond keys"),
    NINTENDO("Nintendo Switch", "Inverted diamond layout, dual thumbsticks, and +/- buttons"),
    STEERING_WHEEL("Steering Wheel", "Virtual rotating wheel, pedals, paddle shifters & gyro tilt")
}
