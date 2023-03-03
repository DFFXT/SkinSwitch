package com.skin.skincore.provider

/**
 * 皮肤包地址提供接口
 */
interface ISkinPathProvider {
    val theme: Int
    fun getSkinPath(): String
}
