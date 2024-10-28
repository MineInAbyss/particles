package com.mineinabyss.particles.systems

import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.systems.query.query
import com.mineinabyss.idofront.operators.minus
import com.mineinabyss.idofront.operators.times
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.particles.components.FollowsNearestPlayer
import com.mineinabyss.particles.components.Position
import com.mineinabyss.particles.components.Velocity
import org.bukkit.Bukkit
import org.bukkit.util.Vector

fun Geary.playerHoming() = system(query<Position, Velocity> { has<FollowsNearestPlayer>() })
    .every(1.ticks)
    .execOnAll {
        val player = Bukkit.getOnlinePlayers().firstOrNull() ?: return@execOnAll
        val target = player.location.toVector()
        val maxSpeed = 0.1
        forEach { (position, velocity) ->
            // update position to home on player's location with
            val distance = target.clone().minus(Vector(position.x, position.y, position.z))
            if(distance == Vector(0, 0, 0)) return@forEach
            val direction = distance.normalize() * maxSpeed
            velocity.dx = velocity.dx * 0.95 + direction.x.coerceIn(-maxSpeed, maxSpeed)
            velocity.dy = velocity.dy * 0.95 + direction.y.coerceIn(-maxSpeed, maxSpeed)
            velocity.dz = velocity.dz * 0.95 + direction.z.coerceIn(-maxSpeed, maxSpeed)
        }
    }
