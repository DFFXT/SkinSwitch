package com.skin.skincore.provider

class CustomSkinPathProvider(
    private val folder: String,
    private val skinName: String,
    override val theme: Int

) : ISkinPathProvider {
    override fun getSkinPath(): String {
        return "$folder/$skinName"
    }
}
