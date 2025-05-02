package org.t_smovzh.easyscriptmod.dialogues

import com.mojang.authlib.minecraft.client.MinecraftClient
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import org.t_smovzh.easyscriptmod.ModServerNetworking
import org.t_smovzh.easyscriptmod.NPCEntity

class PlayerInteractHandler: UseEntityCallback {
    override fun interact(player: PlayerEntity?, world: World?, hand: Hand?, targetEntity: Entity?, hitResult: EntityHitResult?): ActionResult {
        if(player == null || world == null || hand == null || targetEntity == null || hitResult == null)
            return ActionResult.PASS

        if(player.isInSneakingPose && targetEntity is NPCEntity) {
            if(player is ServerPlayerEntity)
                DialogueManager.handle(player, targetEntity)
                

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}