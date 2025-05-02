package org.t_smovzh.easyscriptmod.configs

import net.fabricmc.loader.api.FabricLoader

object ModConfigs {
    data class NpcConfig(val id: String, val width: Double, val height: Double, val modelScale: Double, val name: String, val renderId: String)
    data class Dialogue(
        val id: String, 
        val after: String, 
        val npcIds: Array<String>, 
        val phrases: Array<String>,
        val delay: Int? = null,           
        val nextDialogues: Array<String>? = null,  
        val startDelay: Int? = null       
    )

    var REGISTER_NPCS = arrayOf<NpcConfig>()
    var DIALOGUES = arrayOf<Dialogue>()

    private const val configDir = "scriptMod"
    private const val npcPath = "npcs"
    private const val dialoguePath = "dialogues"

    fun loadConfigs() {
        val npcsFiles = FabricLoader.getInstance().configDir.resolve(configDir).resolve(npcPath).toFile().listFiles()

        if (npcsFiles != null) {
            REGISTER_NPCS = Array(npcsFiles.size){
                readJson("$configDir/$npcPath/${npcsFiles[it].name.dropLast(".json".length)}", NpcConfig::class)
            }
        }

        val dialogueFiles = FabricLoader.getInstance().configDir.resolve(configDir).resolve(dialoguePath).toFile().listFiles()

        if(dialogueFiles != null){
            DIALOGUES = Array(dialogueFiles.size){
                readJson("$configDir/$dialoguePath/${dialogueFiles[it].name.dropLast(".json".length)}", Dialogue::class)
            }
        }
    }
}
