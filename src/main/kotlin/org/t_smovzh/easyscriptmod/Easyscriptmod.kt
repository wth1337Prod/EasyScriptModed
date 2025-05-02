package org.t_smovzh.easyscriptmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.entity.mob.PathAwareEntity
import org.t_smovzh.easyscriptmod.configs.ModConfigs
import org.t_smovzh.easyscriptmod.dialogues.DialogueManager
import org.t_smovzh.easyscriptmod.dialogues.PlayerInteractHandler
import java.util.logging.Level
import java.util.logging.Logger

class Easyscriptmod : ModInitializer {
    companion object {
        val MOD_ID = "easyscriptmod"
        private val LOGGER = Logger.getLogger("EasyScriptMod")

        init {
            try {
                LOGGER.info("Initializing EasyScriptMod")
                ModConfigs.loadConfigs()
                
                
                
                LOGGER.info("EasyScriptMod configs loaded")
            } catch (e: Exception) {
                LOGGER.log(Level.SEVERE, "Failed to initialize mod in companion object", e)
            }
        }
    }

    override fun onInitialize() {
        try {
            LOGGER.info("Starting EasyScriptMod initialization")
            
            NpcManager.registerNpc()
            
            for(i in NpcManager.REGISTORED_NPCS) {
                FabricDefaultAttributeRegistry.register(i, PathAwareEntity.createMobAttributes())
            }

            DialogueManager.register()

            UseEntityCallback.EVENT.register(PlayerInteractHandler())
            
            ServerTickEvents.END_SERVER_TICK.register { server ->
                DialogueManager.update()
            }
            
            LOGGER.info("EasyScriptMod initialized successfully")
        } catch (e: Exception) {
            LOGGER.log(Level.SEVERE, "Failed to initialize mod", e)
        }
    }
}