package com.example.viewdebug.xml.struct.writer.helper.value

import android.util.TypedValue
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.AttrValueFormat

/**
 * 解析boolean
 * The <var>data</var> field holds 0 or 1 that was originally
 * specified as "false" or "true".
 * public static final int TYPE_INT_BOOLEAN = 0x12;
 */
class AttrStringValueCompile : AttrValueCompile(AttrValueFormat.STRING) {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue {
        val index = compiler.addString(attrValue)
        return CompiledAttrValue(TypedValue.TYPE_STRING.toByte(), index, true)
    }
}
