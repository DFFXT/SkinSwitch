package com.example.viewdebug.xml.struct.writer.link

/**
 * 提供xml文本到系统资源的链接
 */
interface ResourceLink {
    fun getAttributeId(prefix: String, attrName: String): Int?

    fun getResourceId(type: String, resourceName: String): Int
}