package com.skin.skincore.provider

class CustomSkinPathProvider(private val path: String) : ISkinPathProvider {
    override fun getSkinPath(): String {
        return path
    }
}
