package com.mineinabyss.particles.components

import java.util.UUID

sealed class RenderAsEntity

data class RenderAsEntitySpawned(val id: Int, var lastPosition: Position)
