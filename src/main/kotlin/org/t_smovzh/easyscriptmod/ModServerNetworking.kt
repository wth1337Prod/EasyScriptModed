package org.t_smovzh.easyscriptmod

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.t_smovzh.easyscriptmod.Easyscriptmod.Companion.MOD_ID

object ModServerNetworking {
    val QUESTION_SCREEN_OPEN_PACKET = Identifier(MOD_ID, "open_question")

    fun openQuestionScreen(player: ServerPlayerEntity, question: Text, variants: Array<Text>){
        val byteBuf = PacketByteBufs.create()

        byteBuf.writeText(question)
        byteBuf.writeInt(variants.size)

        for(i in variants)
            byteBuf.writeText(i)

        ServerPlayNetworking.send(player, QUESTION_SCREEN_OPEN_PACKET, byteBuf)
    }
}