package com.mineinabyss.particles.systems

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.systems.query.query
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.particles.components.Lifetime
import com.mineinabyss.particles.components.Position
import com.mineinabyss.particles.components.Velocity
import it.unimi.dsi.fastutil.longs.LongArrayList

@OptIn(UnsafeAccessors::class)
fun Geary.removeOldEntities() = system(query<Lifetime>())
    .every(5.ticks)
    .execOnAll {
        val currentTime = System.currentTimeMillis()
        val queuedRemoval = LongArrayList()
        forEach { (lifetime) ->
            if (currentTime - lifetime.created > lifetime.lifetime) {
                queuedRemoval.add(unsafeEntity.toLong())
            }
        }
        queuedRemoval.forEach { unsafeEntity ->
            unsafeEntity.toGeary().removeEntity()
        }
    }
