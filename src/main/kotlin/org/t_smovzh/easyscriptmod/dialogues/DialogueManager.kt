package org.t_smovzh.easyscriptmod.dialogues

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import org.t_smovzh.easyscriptmod.NPCEntity
import org.t_smovzh.easyscriptmod.configs.ModConfigs
import java.util.LinkedList
import java.util.Queue
import java.util.logging.Logger

object DialogueManager {
    private val logger = Logger.getLogger("EasyScriptMod")
    
    class Dialogue(
        internal val npcs: Array<String>, 
        val id: String, 
        val afterId: String, 
        val phrases: Queue<String>,
        val delay: Int = 0,           
        val nextDialogues: Array<String> = arrayOf(),  
        val startDelay: Int = 0       
    ){
        fun isNpc(npc: String): Boolean{
            for(i in npcs)
                if(i.equals(npc))
                    return true

            return false
        }
    }

    private fun <T: Any> arrayToQueue(list: Array<T>): Queue<T>{
        val result = LinkedList<T>()

        for(i in list)
            result.add(i)

        return result
    }

    private var dialoguesQueue = arrayListOf<Queue<Dialogue>>()
    private var dialoguesMap = mutableMapOf<String, Dialogue>() 
    private var delayTimers = mutableMapOf<Queue<Dialogue>, Int>() 
    private var pendingDialogues = mutableMapOf<String, Int>() 
    private var npcDialogueMap = mutableMapOf<String, ArrayList<Queue<Dialogue>>>() 
    private var cyclicDependencies = mutableListOf<Pair<String, String>>()

    fun register(){
        dialoguesQueue.clear()
        dialoguesMap.clear()
        delayTimers.clear()
        pendingDialogues.clear()
        npcDialogueMap.clear()
        cyclicDependencies.clear()
        
        val remainDialogs = LinkedList<Dialogue>()

        
        for(i in ModConfigs.DIALOGUES) {
            val dialogue = Dialogue(
                i.npcIds, 
                i.id, 
                i.after, 
                arrayToQueue(i.phrases),
                i.delay ?: 0,         
                i.nextDialogues ?: arrayOf(),  
                i.startDelay ?: 0     
            )
            remainDialogs.add(dialogue)
            dialoguesMap[i.id] = dialogue  
        }

        
        val visited = mutableSetOf<String>()
        val stack = mutableSetOf<String>()
        
        for (dialogue in dialoguesMap.values) {
            if (!visited.contains(dialogue.id)) {
                detectCycle(dialogue.id, visited, stack, null)
            }
        }

        if (cyclicDependencies.isNotEmpty()) {
            logger.warning("========== ВНИМАНИЕ: ОБНАРУЖЕНЫ ЦИКЛИЧЕСКИЕ ЗАВИСИМОСТИ ==========")
            for ((from, to) in cyclicDependencies) {
                logger.warning("Цикл между диалогами: $from -> $to")
            }
            logger.warning("Циклы могут привести к бесконечной последовательности диалогов!")
            logger.warning("================================================================")
        }

        while (!remainDialogs.isEmpty()){
            val current = remainDialogs.first

            if(current.afterId == "") {
                val queue = LinkedList<Dialogue>()

                queue.add(current)
                delayTimers[queue] = 0

                dialoguesQueue.add(queue)

                registerNpcDialogue(current, queue)

                remainDialogs.remove()

                continue
            }

            val suitableDialogs = dialoguesQueue.filter { it.last().id == current.afterId }

            for(i in suitableDialogs)
                i.add(current)

            if(suitableDialogs.isNotEmpty())
                remainDialogs.remove()
            else
                remainDialogs.add(remainDialogs.remove())
        }

        optimizeNpcDialogueMap()
        
        logger.info("Система диалогов инициализирована:")
        logger.info("- Загружено диалогов: ${dialoguesMap.size}")
        logger.info("- Активных очередей диалогов: ${dialoguesQueue.size}")
        logger.info("- NPC с диалогами: ${npcDialogueMap.size}")
    }

    
    private fun detectCycle(
        dialogueId: String, 
        visited: MutableSet<String>, 
        stack: MutableSet<String>,
        parentId: String?
    ): Boolean {
        if (stack.contains(dialogueId)) {
            if (parentId != null) {
                cyclicDependencies.add(Pair(parentId, dialogueId))
            }
            return true
        }
        
        if (visited.contains(dialogueId)) {
            return false
        }
        
        visited.add(dialogueId)
        stack.add(dialogueId)
        
        val dialogue = dialoguesMap[dialogueId] ?: return false
        
        
        for (nextId in dialogue.nextDialogues) {
            if (detectCycle(nextId, visited, stack, dialogueId)) {
                return true
            }
        }
        
        
        if (dialogue.afterId.isNotEmpty()) {
            val afterDialogue = dialoguesMap[dialogue.afterId]
            if (afterDialogue != null) {
                if (detectCycle(afterDialogue.id, visited, stack, dialogueId)) {
                    return true
                }
            }
        }
        
        stack.remove(dialogueId)
        return false
    }

    
    private fun startDialogue(dialogueId: String) {
        val dialogue = dialoguesMap[dialogueId] ?: return
        
        val isCyclic = cyclicDependencies.any { it.first == dialogueId || it.second == dialogueId }
        if (isCyclic) {
            logger.warning("Попытка запуска диалога $dialogueId, который участвует в циклической зависимости.")
        }
        
        if (dialogue.startDelay > 0) {
            
            pendingDialogues[dialogueId] = dialogue.startDelay
            return
        }
        
        
        val queue = LinkedList<Dialogue>()
        queue.add(dialogue)
        delayTimers[queue] = 0
        
        dialoguesQueue.add(queue)
        
        
        registerNpcDialogue(dialogue, queue)
    }

    
    private fun handleDialogueCompletion(dialogue: Dialogue, queue: Queue<Dialogue>) {
        
        for (npcId in dialogue.npcs) {
            npcDialogueMap[npcId]?.remove(queue)
        }
        
        
        for (nextDialogueId in dialogue.nextDialogues) {
            val willCreateInfiniteCycle = cyclicDependencies.any { 
                it.first == dialogue.id && it.second == nextDialogueId 
            }
            
            if (willCreateInfiniteCycle) {
                logger.warning("Предотвращено бесконечное зацикливание: ${dialogue.id} -> $nextDialogueId")
                continue
            }
            
            startDialogue(nextDialogueId)
        }
        
        if (queue.isEmpty()) {
            optimizeNpcDialogueMap()
        }
    }

    
    fun update() {
        
        val keysToUpdate = delayTimers.keys.toList()
        for (queue in keysToUpdate) {
            if (queue.isEmpty()) {
                delayTimers.remove(queue)
                continue
            }
            
            val timer = delayTimers[queue] ?: 0
            if (timer > 0) {
                delayTimers[queue] = timer - 1
            }
        }
        
        
        val pendingToUpdate = pendingDialogues.keys.toList()
        for (dialogueId in pendingToUpdate) {
            val timer = pendingDialogues[dialogueId] ?: 0
            if (timer <= 1) {
                pendingDialogues.remove(dialogueId)
                startDialogue(dialogueId)
            } else {
                pendingDialogues[dialogueId] = timer - 1
            }
        }
    }

    fun handle(player: PlayerEntity, entity: NPCEntity){
        val npcId = entity.npcID
        val npcDialogues = npcDialogueMap[npcId] ?: return
        
        
        for (queue in npcDialogues) {
            if (queue.isEmpty())
                continue

            val dialogue = queue.first()
            val timer = delayTimers[queue] ?: 0

            if (timer <= 0) {
                player.sendMessage(Text.literal(dialogue.phrases.remove()))

                if (dialogue.phrases.isEmpty()) {
                    
                    val completedDialogue = dialogue
                    queue.remove()
                    
                    
                    handleDialogueCompletion(completedDialogue, queue)
                } else {
                    
                    delayTimers[queue] = dialogue.delay
                }

                return
            }
        }
    }

    private fun registerNpcDialogue(dialogue: Dialogue, queue: Queue<Dialogue>) {
        for (npcId in dialogue.npcs) {
            if (!npcDialogueMap.containsKey(npcId)) {
                npcDialogueMap[npcId] = arrayListOf()
            }
            npcDialogueMap[npcId]?.add(queue)
        }
    }
    
    private fun optimizeNpcDialogueMap() {
        val npcIds = npcDialogueMap.keys.toList()
        for (npcId in npcIds) {
            val queues = npcDialogueMap[npcId]
            if (queues != null) {
                val filteredQueues = queues.filter { !it.isEmpty() }
                if (filteredQueues.isEmpty()) {
                    npcDialogueMap.remove(npcId)
                } else {
                    npcDialogueMap[npcId] = ArrayList(filteredQueues)
                }
            }
        }
    }

    fun forceStartDialogue(dialogueId: String) {
        startDialogue(dialogueId)
    }
    
    fun getDialogueSystemInfo(): Map<String, Any> {
        return mapOf(
            "totalDialogues" to dialoguesMap.size,
            "activeQueues" to dialoguesQueue.size,
            "npcWithDialogues" to npcDialogueMap.size,
            "pendingDialogues" to pendingDialogues.size,
            "cyclicDependencies" to cyclicDependencies.size
        )
    }
}