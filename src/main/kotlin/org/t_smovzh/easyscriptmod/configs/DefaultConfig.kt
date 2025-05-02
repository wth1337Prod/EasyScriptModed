package org.t_smovzh.easyscriptmod.configs

interface DefaultConfig {
    fun get(namespace: String?): String?
}

class EmptyConfig: DefaultConfig{
    override fun get(namespace: String?) = ""
}