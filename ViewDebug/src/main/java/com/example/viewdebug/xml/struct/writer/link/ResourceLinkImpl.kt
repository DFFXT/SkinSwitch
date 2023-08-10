package com.example.viewdebug.xml.struct.writer.link

import android.content.Context

/**
 * 资源链接，将属性、资源等转换成当前项目的对应的id
 */
class ResourceLinkImpl(private val ctx: Context) : ResourceLink {
    override fun getAttributeId(prefix: String, attrName: String): Int {
        return when (prefix) {
            "android" -> {
                var id = ctx.resources.getIdentifier(attrName, "attr", prefix)
                if (id == 0) {
                    id = LinkPlugin.getAttributeId(prefix, attrName)
                }
                if (id == 0) {
                    throw Exception("未找到$prefix:${attrName}对应的id， 请使用LinkPlugin.addSupportedAttribute添加属性")
                }
                id
            }

            "app" -> {
                var id = ctx.resources.getIdentifier(attrName, "attr", ctx.packageName)
                if (id == 0) {
                    id = LinkPlugin.getResourceId(prefix, attrName)
                }
                if (id == 0) {
                    throw Exception("未找到$prefix:${attrName}对应的id, 请使用请使用LinkPlugin.addSupportedResource添加资源")
                }
                id
            }

            else -> {
                Int.MAX_VALUE
            }
        }
    }

    /**
     * 格式：
     * @android:color/xxx
     * ?android:xxxx
     * @color/xxxx
     */
    override fun getResourceId(type: String, resourceName: String): Int {
        return if (type.startsWith("android:")) {
            return ctx.resources.getIdentifier(resourceName, type, "android")
        } else if (type.startsWith("?android")){
            0
        } else {
            return ctx.resources.getIdentifier(resourceName, type, ctx.packageName)
        }
    }
}