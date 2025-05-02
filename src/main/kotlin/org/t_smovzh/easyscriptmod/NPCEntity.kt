package org.t_smovzh.easyscriptmod

import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.text.Text
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil

class NPCEntity(entityType: EntityType<out NPCEntity>, world: World?, private val _npcName: String, val npcID: String) : PathAwareEntity(entityType, world), GeoEntity {
    private val cache by lazy { GeckoLibUtil.createInstanceCache(this) }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar?) {

    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return cache
    }

    override fun isAttackable(): Boolean {
        return false
    }

    override fun getName(): Text {
        return Text.of(_npcName)
    }
}