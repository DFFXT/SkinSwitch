package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.ReferenceType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

/**
 * 解析引用格式
 * @+id/xxx
 * @id/xxx
 * @color/xxx
 */
class AttReferenceValueCompile : AttrValueCompile("reference") {
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
        if (end > 0) {
            var valueRowType = attrValue.substring(1, end)
            // 资源类型见[ResourceFolderType]
            val valueRowValue = attrValue.substring(end + 1)
            val ctx = ViewDebugInitializer.ctx
            if (valueRowType == "+id") {
                // 单独判断@+id的情况
                valueRowType = ReferenceType.TYPE_ID
            }
            var id = ctx.resources.getIdentifier(valueRowValue, valueRowType, ctx.packageName)
            if (id == 0 && valueRowType == ReferenceType.TYPE_ID) {
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

            }
            // todo
            return CompiledAttrValue(ResourceType.TYPE_REFERENCE, id)
        }
        return null
    }
}
