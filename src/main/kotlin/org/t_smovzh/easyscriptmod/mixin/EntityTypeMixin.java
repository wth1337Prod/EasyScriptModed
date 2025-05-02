package org.t_smovzh.easyscriptmod.mixin;

import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Миксин для исправления проблемы загрузки класса EntityType
 */
@Mixin(EntityType.class)
public class EntityTypeMixin {
} 