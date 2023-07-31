package com.example.viewdebug.xml.struct.writer.link

/**
 * 由于版本问题，某些属性可能无法通过反射R类或者调用getIdentifier方法获取
 * 此时需要单独配置
 */
object LinkPlugin:ResourceLink {
    private val highBuildVersionAttribute = HashMap<String, Int>()
    private val highBuildVersionResource = HashMap<String, Int>()

    init {
        // 至少在api25版本上无法支持这些属性
        highBuildVersionAttribute["android:paddingHorizontal"] = android.R.attr.paddingHorizontal
        highBuildVersionAttribute["android:paddingVertical"] = android.R.attr.paddingVertical


    }
    override fun getAttributeId(prefix: String, attrName: String): Int {
        return highBuildVersionAttribute["$prefix:$attrName"] ?: 0
    }

    override fun getResourceId(type: String, resourceName: String): Int {
        return highBuildVersionResource["$type:$resourceName"]?:0
    }

    /**
     * 新增高版本属性或者自定义属性
     */
    fun addSupportedAttribute(prefix: String,attrName: String, id: Int) {
        highBuildVersionAttribute["$prefix:$attrName"] = id
    }

    /**
     * 新增资源id，可做资源拦截修改
     */
    fun addSupportedResource(type: String, resourceName: String, id: Int) {
        highBuildVersionResource["$type:$resourceName"] = id
    }
}