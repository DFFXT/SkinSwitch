package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

/**
 * 解析color格式
 */
class AttReferenceValueCompile : AttrValueCompile("reference") {
    override fun compile(attrValue: String): Pair<Byte, Int>? {
        // 是引用类型
        val end = attrValue.indexOf('/')
        if (end > 0) {
            val valueRowType = attrValue.substring(1, end)
            // 资源类型见[ResourceFolderType]
            val valueRowValue = attrValue.substring(end + 1)
            val ctx = ViewDebugInitializer.ctx
            val id = ctx.resources.getIdentifier(valueRowValue, valueRowType, ctx.packageName)
            // todo
            return Pair(ResourceType.TYPE_REFERENCE, id)
        }
        return null
    }
}
