package com.mineinabyss.particles.systems

import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.systems.query.query
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.particles.components.Position
import com.mineinabyss.particles.components.Velocity

fun Geary.updatePosition() = system(query<Position, Velocity>())
    .every(1.ticks)
    .exec { (position, velocity) ->
        position.x += velocity.dx
        position.y += velocity.dy
        position.z += velocity.dz
    }
