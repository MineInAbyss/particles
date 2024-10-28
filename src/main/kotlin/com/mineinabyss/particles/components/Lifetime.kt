package com.mineinabyss.particles.components

import kotlin.time.Duration

data class Lifetime(val created: Long, val lifetime: Long) {
    companion object {
        fun create(duration: Duration) = Lifetime(System.currentTimeMillis(), duration.inWholeMilliseconds)
    }
}
