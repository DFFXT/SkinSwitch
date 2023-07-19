package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.ReferenceType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType
import java.nio.ByteBuffer

class AttrFloatValueCompile : AttrValueCompile(FormatType.TYPE_FLOAT) {
    private val buffer = ByteBuffer.allocate(4)
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        val float = attrValue.toFloatOrNull()
        return if (float != null) {
            // 将浮点数转换成整数
            buffer.putFloat(float)
            buffer.position(0)
            CompiledAttrValue(ResourceType.TYPE_FLOAT, buffer.int)
        } else {
            null
        }
    }

}