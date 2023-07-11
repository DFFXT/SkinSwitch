package com.example.viewdebug.xml.struct.writer

import android.content.Context

class ResourceLinkImpl(private val ctx:Context): ResourceLink {
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
}