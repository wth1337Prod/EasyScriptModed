package org.t_smovzh.easyscriptmod

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.t_smovzh.easyscriptmod.configs.ModConfigs

object NpcManager {
    var REGISTORED_NPCS = arrayOf<EntityType<NPCEntity>>()

    fun registerNpc(){
        REGISTORED_NPCS = Array(ModConfigs.REGISTER_NPCS.size){
            val config = ModConfigs.REGISTER_NPCS[it]

            val registry = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of("scriptsnpcs", config.id),
                EntityType.Builder.create({ entityType: EntityType<NPCEntity>, world: World? -> NPCEntity(entityType, world, config.name, config.id)}, SpawnGroup.MISC)
                    .setDimensions(config.width.toFloat(), config.height.toFloat())
                    .build(config.id))

            registry
        }
    }
}