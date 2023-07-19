package com.example.viewdebug.xml.struct.writer.helper.value

import android.util.TypedValue
import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.XmlCompiler

/**
 * 解析boolean
 * The <var>data</var> field holds 0 or 1 that was originally
 * specified as "false" or "true".
 * public static final int TYPE_INT_BOOLEAN = 0x12;
 */
class AttrBooleanValueCompile : AttrValueCompile(FormatType.TYPE_BOOLEAN) {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {

        return if (attrValue.equals("true", true)) {
            CompiledAttrValue(TypedValue.TYPE_INT_BOOLEAN.toByte(), 1)
        } else if (attrValue.equals("false", true)) {
            CompiledAttrValue(TypedValue.TYPE_INT_BOOLEAN.toByte(), 0)
        } else {
            null
        }
    }
}
