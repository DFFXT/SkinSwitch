package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.ReferenceType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

/**
 * 解析引用格式
 * @+id/xxx
 * @id/xxx
 * @color/xxx
 */
open class AttReferenceValueCompile : AttrValueCompile(FormatType.TYPE_REFERENCE) {
    /**
     * id模板，如果更新的布局中新增了id，ConstraintLayout布局中很常见，那么就用这些替换，最多支持15个新增id
     */
    private val idTemplate by lazy {
        intArrayOf(
            R.id.view_debug_template_id_1,
            R.id.view_debug_template_id_2,
            R.id.view_debug_template_id_3,
            R.id.view_debug_template_id_4,
            R.id.view_debug_template_id_5,
            R.id.view_debug_template_id_6,
            R.id.view_debug_template_id_7,
            R.id.view_debug_template_id_8,
            R.id.view_debug_template_id_9,
            R.id.view_debug_template_id_10,
            R.id.view_debug_template_id_11,
            R.id.view_debug_template_id_12,
            R.id.view_debug_template_id_13,
            R.id.view_debug_template_id_14,
            R.id.view_debug_template_id_15,
        )
    }
    // 当前使用的id下标
    private var idIndex = 0
    // key: id 名称，value：id值
    private val idMap = HashMap<String, Int>()
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        // 是引用类型
        val end = attrValue.indexOf('/')
        // 引用类型：
        // 1: @color/xxx
        // 2: @+id/xxx
        // 3: @null
        // 4: ?attr/xxx
        // 5: ?android:attr/xxx
        // 6: ?xxxx
        if (end > 0 && attrValue.startsWith("@") || attrValue.startsWith("?")) {
            // 判定类型，如果是？开头，则是attribute类型
            val dataType = if (attrValue.startsWith("@")) {
                ResourceType.TYPE_REFERENCE
            } else {
                ResourceType.TYPE_ATTRIBUTE
            }
            // 如果end < 0，则说明是?xxx的形式
            var valueRowType = if (end > 0) {
                attrValue.substring(1, end)
            } else "attr"
            // 资源类型见[ResourceFolderType]
            val valueRowValue = if (end > 0) {
                attrValue.substring(end + 1)
            } else attrValue.substring(1)
            val ctx = ViewDebugInitializer.ctx
            if (valueRowType == "+id") {
                // 单独判断@+id的情况
                valueRowType = ReferenceType.TYPE_ID
            }
            // 根据是否有冒号判定，类型字符串里面是否有包名(比如?android:attr/xxx)
            val pkgIndex = valueRowType.indexOf(':')
            val pkgName:String
            val type: String
            if (pkgIndex > 0) {
                pkgName = valueRowType.substring(0, pkgIndex)
                type = valueRowType.substring(pkgIndex + 1)
            } else {
                pkgName = ctx.packageName
                type = valueRowType
            }
            var id = ctx.resources.getIdentifier(valueRowValue, type, pkgName)
            if (id == 0) {
                if (valueRowType == ReferenceType.TYPE_ID) {
                    // 是新增的id
                    var reflexId = idMap[valueRowValue]
                    // 需要使用新的id
                    if (reflexId == null) {
                        if (idIndex >= idTemplate.size) {
                            throw Exception("increase new id over 15")
                        } else {
                            reflexId = idTemplate[idIndex]
                            idMap[valueRowValue] = reflexId
                            idIndex++
                        }
                    }
                    id = reflexId
                } else {
                    // 其它类型或者格式问题
                    return null
                }

            }
            return CompiledAttrValue(dataType, id)
        } else if (attrValue == "@null") {
            return CompiledAttrValue(ResourceType.TYPE_REFERENCE, 0)
        }
        return null
    }
}
