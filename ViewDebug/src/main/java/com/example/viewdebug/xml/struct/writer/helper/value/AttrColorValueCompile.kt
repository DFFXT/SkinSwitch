package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType
import java.lang.StringBuilder

/**
 * 解析color格式
 * 通过对比正确格式，value必须是完整的颜色值，即必须包含ARGB的32位无符号整形
 */
class AttrColorValueCompile : AttrValueCompile("color") {
    private val builder = StringBuilder()
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        if (attrValue.startsWith("#")) {


            return when (attrValue.length) {
                // #f00
                4 -> {
                    // 主动添加alpha
                    builder.append("FF")
                    builder.append(attrValue[1])
                    builder.append(attrValue[1])
                    builder.append(attrValue[2])
                    builder.append(attrValue[2])
                    builder.append(attrValue[3])
                    builder.append(attrValue[3])
                    val color = builder.toString().toUInt(16)
                    //val value = Integer.valueOf(builder.toString(), 16)
                    CompiledAttrValue(ResourceType.TYPE_INT_COLOR_RGB4, color.toInt())
                }
                // #ff00
                5 -> {
                    builder.append(attrValue[1])
                    builder.append(attrValue[1])
                    builder.append(attrValue[2])
                    builder.append(attrValue[2])
                    builder.append(attrValue[3])
                    builder.append(attrValue[3])
                    builder.append(attrValue[4])
                    builder.append(attrValue[4])
                    val color = builder.toString().toUInt(16).toInt()
                    // val value = Integer.valueOf(builder.toString(), 16)
                    CompiledAttrValue(ResourceType.TYPE_INT_COLOR_ARGB4, color)
                }
                // #ff0000
                7 -> {
                    //val value = Integer.valueOf(attrValue.substring(1), 16)
                    builder.append("FF")
                    builder.append(attrValue.substring(1))

                    val value = builder.toString().toUInt(16).toInt()
                    CompiledAttrValue(ResourceType.TYPE_INT_COLOR_RGB8, value)
                }
                // #ff00ff00
                9 -> {
                    //val value = Integer.valueOf(attrValue.substring(1), 16)
                    val value = attrValue.substring(1).toUInt(16).toInt()
                    CompiledAttrValue(ResourceType.TYPE_INT_COLOR_ARGB8, value)
                }
                // ???
                else -> {
                    val value = attrValue.substring(1).toUInt(16).toInt()
                    CompiledAttrValue(ResourceType.TYPE_FIRST_COLOR_INT, value)
                }
            }
        }
        return null
    }
}
