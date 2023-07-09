package com.example.viewdebug.xml.struct.writer.helper.value

import android.util.TypedValue
import android.util.TypedValue.COMPLEX_MANTISSA_MASK
import android.util.TypedValue.COMPLEX_MANTISSA_SHIFT
import android.util.TypedValue.COMPLEX_RADIX_SHIFT
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ExternalFunction
import com.example.viewdebug.xml.struct.writer.helper.ResourceType
import java.nio.ByteBuffer

/**
 * dimension编译
 * 参见[TypedValue.complexToFloat], 将编译后的数据解析成float数据
 * 那么需要根据解析方式反推编译方式
 * [TypedValue.COMPLEX_UNIT_MASK] 后8位数据是单位[TypedValue.COMPLEX_UNIT_DIP]等
 *
 *
 * 0、根据[TypedValue.complexToFloat]反推：
 * （右侧低位）
 * int 32位
 * <11111111 11111111 11111111:系数><11:不知道干啥的><11:基数><1111:单位>
 * 系数*基数=原始值
 *
 * 1、android系统读取方式：
 * 取单位，最后四位为单位， data and 0xF
 * 取基数，data >> 4 and 0x11
 * 取系数，data and 0xFFFFFF00
 *
 * 2、int编译过程
 * int 数据编译过程
 * 值： int/基数<<8   or   基数<<4   or   单位
 * 其中，int/基数<<8不能溢出，否则会丢失数据，如果溢出了就需要换基数
 *
 * 3、浮点数编译过程
 * 当需要对浮点数进行编译时，需要将浮点数转换为int
 * 转换方式：用ByteBuffer.putFloat存浮点数，用getInt读就转换成功了
 * 然后再走int编译步骤
 *
 */
class AttrDimensionValueCompile : AttrValueCompile("dimension") {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue {
        val intEnum = attrValue.toIntOrNull()
        return if (intEnum != null) {
            CompiledAttrValue(ResourceType.TYPE_FIRST_INT, intEnum)
        } else {
            // 前32位存储类型，后32位存储data
            val typeAndData = ExternalFunction.stringToFloat(attrValue)
            CompiledAttrValue((typeAndData shr 32).toByte(), (typeAndData and 0xFFFFFFFF).toInt())
        }
    }




}