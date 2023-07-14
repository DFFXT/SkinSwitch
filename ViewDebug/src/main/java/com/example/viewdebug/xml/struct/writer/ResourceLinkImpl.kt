package com.example.viewdebug.xml.struct.writer

import android.content.Context

class ResourceLinkImpl(private val ctx: Context) : ResourceLink {
    override fun getAttributeId(prefix: String, attrName: String): Int {
        return when (prefix) {
            "android" -> {
                ctx.resources.getIdentifier(attrName, "attr", prefix)
            }

            "app" -> {
                ctx.resources.getIdentifier(attrName, "attr", ctx.packageName)
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