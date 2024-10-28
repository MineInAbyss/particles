package com.mineinabyss.particles.systems.rendering

import com.mineinabyss.geary.engine.archetypes.EntityRemove
import com.mineinabyss.geary.helpers.fastForEach
import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.modules.observe
import com.mineinabyss.geary.observers.events.OnAdd
import com.mineinabyss.geary.observers.events.OnEntityRemoved
import com.mineinabyss.geary.systems.query.query
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.particles.components.Position
import com.mineinabyss.particles.components.RenderAsEntity
import com.mineinabyss.particles.components.RenderAsEntitySpawned
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Display.TAG_POS_ROT_INTERPOLATION_DURATION
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.craftbukkit.CraftParticle
import org.bukkit.craftbukkit.block.data.CraftBlockData
import java.util.*

const val idOffset = 1000000

// access private static field BlockDisplay.DATA_BLOCK_STATE_ID via reflection
val dataBlockStateId = Display.BlockDisplay::class.java.getDeclaredField("DATA_BLOCK_STATE_ID").apply {
    isAccessible = true
}.get(null) as EntityDataAccessor<BlockState>

val DATA_POS_ROT_INTERPOLATION_DURATION_ID = Display::class.java.getDeclaredField("DATA_POS_ROT_INTERPOLATION_DURATION_ID").apply {
    isAccessible = true
}.get(null) as EntityDataAccessor<Int>

fun Geary.spawnMinecraftEntities() = observe<OnAdd>().involving<Position, RenderAsEntity>().exec(
    query<Position>()
) { (position) ->
    val world = Bukkit.getWorld("world")!!
    val players = world.players
    val entityId = idOffset + entity.id.toInt()
    val entityAdd = ClientboundAddEntityPacket(
        entityId,
        UUID.randomUUID(),
        position.x,
        position.y,
        position.z,
        0f,
        0f,
        NMSEntityType.BLOCK_DISPLAY,
        0,
        Vec3.ZERO,
        0.0
    )
    // get block state for STONE
    val state = (Material.STONE.createBlockData() as CraftBlockData).state
    val entityData = ClientboundSetEntityDataPacket(
        entityId, listOf<SynchedEntityData.DataValue<*>>(
            SynchedEntityData.DataValue.create(dataBlockStateId, state),
            SynchedEntityData.DataValue.create(DATA_POS_ROT_INTERPOLATION_DURATION_ID, 10)
        )
    )
    players.fastForEach {
        it.toNMS().connection.send(ClientboundBundlePacket(listOf(entityAdd, entityData)))
    }
    entity.set(RenderAsEntitySpawned(entityId, position.copy()))
}

fun Geary.moveMinecraftEntities() = system(query<Position, RenderAsEntitySpawned>())
    .every(1.ticks)
    .execOnAll {
        val world = Bukkit.getWorld("world")!!
        val players = world.players
        map { (position, spawned) ->
            val dx = (position.x * 4096 - spawned.lastPosition.x * 4096).toInt().toShort()
            val dy = (position.y * 4096 - spawned.lastPosition.y * 4096).toInt().toShort()
            val dz = (position.z * 4096 - spawned.lastPosition.z * 4096).toInt().toShort()
            spawned.lastPosition = position.copy()
            ClientboundMoveEntityPacket.Pos(spawned.id, dx, dy, dz, true)
        }.chunked(MAX_BUNDLE_SIZE).shuffled().take(10).fastForEach { chunk ->
            players.fastForEach {
                it.toNMS().connection.send(
                    ClientboundBundlePacket(chunk)
                )
            }
        }
    }

fun Geary.removeAssociatedMinecraftEntity() = observe<OnEntityRemoved>().exec(query<RenderAsEntitySpawned>()) { (spawned) ->
    val world = Bukkit.getWorld("world")!!
    val players = world.players
    players.fastForEach {
        it.toNMS().connection.send(ClientboundRemoveEntitiesPacket(spawned.id))
    }
}
