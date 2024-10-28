package com.mineinabyss.particles

import com.mineinabyss.geary.addons.dsl.createAddon
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.papermc.configure
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.playerExecutes
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.particles.components.*
import com.mineinabyss.particles.systems.playerHoming
import com.mineinabyss.particles.systems.removeOldEntities
import com.mineinabyss.particles.systems.rendering.moveMinecraftEntities
import com.mineinabyss.particles.systems.rendering.removeAssociatedMinecraftEntity
import com.mineinabyss.particles.systems.rendering.spawnMinecraftEntities
import com.mineinabyss.particles.systems.rendering.spawnMinecraftParticles
import com.mineinabyss.particles.systems.updatePosition
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val ParticlesAddon = createAddon("Particles") {
    systems {
        updatePosition()
        playerHoming()
        spawnMinecraftParticles()
        removeOldEntities()
        spawnMinecraftEntities()
        moveMinecraftEntities()
        removeAssociatedMinecraftEntity()
    }
}

class ParticlesPlugin : JavaPlugin() {
    override fun onLoad() {
        gearyPaper.configure {
            install(ParticlesAddon)
        }

        commands {
            "particles" {
                val amount = Args.integer(min = 1).default { 1 }.named("amount")
                "follow" {
                    playerExecutes(amount) { amount ->
                        with(player.world.toGeary()) {
                            val (x, y, z) = player.location
                            repeat(amount) {
                                entity {
                                    set(Position(x, y, z))
                                    set(Velocity(Random.nextDouble(-3.0, 3.0),Random.nextDouble(-3.0, 3.0),Random.nextDouble(-3.0, 3.0)))
                                    add<RenderAsEntity>()
                                    set(Lifetime.create(10.seconds))
                                    add<FollowsNearestPlayer>()
                                }
                            }
                        }
                    }
                }
                "velocity" {
                    playerExecutes(amount) { amount ->
                        with(player.world.toGeary()) {
                            val (x, y, z) = player.location
                            repeat(amount) {
                                entity {
                                    set(Position(x, y, z))
                                    set(
                                        Velocity(
                                            Random.nextDouble(-3.0, 3.0),
                                            Random.nextDouble(-3.0, 3.0),
                                            Random.nextDouble(-3.0, 3.0)
                                        )
                                    )
                                    add<RenderAsEntity>()
                                    set(Lifetime.create(10.seconds))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

