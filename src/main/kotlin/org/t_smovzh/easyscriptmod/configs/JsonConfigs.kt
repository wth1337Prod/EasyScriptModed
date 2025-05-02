package org.t_smovzh.easyscriptmod.configs

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import java.util.*
import kotlin.reflect.KClass

fun <T : Any> readJson(path: String, outClass: KClass<T>): T {
    val scaner = Scanner(FabricLoader.getInstance().configDir.resolve("$path.json").toFile())

    var fileStr = "";

    while (scaner.hasNextLine()) fileStr += scaner.nextLine()

    val gson = Gson()

    return gson.fromJson(fileStr, outClass.java)
}