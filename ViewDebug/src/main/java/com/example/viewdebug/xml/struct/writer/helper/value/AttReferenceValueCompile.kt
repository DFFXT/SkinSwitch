package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

/**
 * 解析引用格式
 * @+id/xxx
 * @id/xxx
 * @color/xxx
 */
class AttReferenceValueCompile : AttrValueCompile("reference") {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        // 是引用类型
        val end = attrValue.indexOf('/')
        if (end > 0) {
            var valueRowType = attrValue.substring(1, end)
            // 资源类型见[ResourceFolderType]
            val valueRowValue = attrValue.substring(end + 1)
            val ctx = ViewDebugInitializer.ctx
            if (valueRowType == "+id") {
                // 单独判断@+id的情况
                valueRowType = "id"
            }
            val id = ctx.resources.getIdentifier(valueRowValue, valueRowType, ctx.packageName)
            // todo
            return CompiledAttrValue(ResourceType.TYPE_REFERENCE, id)
        }
        return null
    }
}
