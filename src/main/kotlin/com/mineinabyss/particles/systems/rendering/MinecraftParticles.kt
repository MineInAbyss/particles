package com.mineinabyss.particles.systems.rendering

import com.mineinabyss.geary.helpers.fastForEach
import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.systems.query.query
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.particles.components.Position
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.craftbukkit.CraftParticle

const val MAX_BUNDLE_SIZE = 4096

fun Geary.spawnMinecraftParticles() = system(query<Position, Particle>())
    .every(1.ticks)
    .execOnAll {
        val world = Bukkit.getWorld("world")!!
            val param = CraftParticle.createParticleParam(Particle.FLAME, null)
        val players = world.players
        // TODO chunked call
            map { (position, particle) ->
                ClientboundLevelParticlesPacket(param, false, position.x, position.y, position.z, 0f, 0f, 0f, 0f, 1)
            }.chunked(MAX_BUNDLE_SIZE).shuffled().take(10).fastForEach { chunk ->
                players.fastForEach {
                    it.toNMS().connection.send(
                        ClientboundBundlePacket(chunk)
                    )
                }
            }
    }
