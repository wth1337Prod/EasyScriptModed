package org.t_smovzh.easyscriptmod.configs

class ModConfigProvider: DefaultConfig {
    private var configContents = ""

    fun addDefaultConfig(key: String, value: Any, comment: String = "") {
        configContents += "$key=$value${(if(comment != "") " #$comment" else "")}\n"
    }

    override fun get(namespace:String?): String? {
        return configContents
    }
}
