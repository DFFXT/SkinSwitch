package com.example.viewdebug.ui.skin

import android.content.res.AssetManager
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.skin.log.Logger
import com.skin.skincore.SkinManager

/**
 * 应用资源变更管理
 */
object ViewDebugResourceManager {


    private val interceptorMapper = HashSet<Int>()

    /**
     * 支持的类型
     */
    val VALUE_TYPE = arrayOf("string", "color", "dimen")

    /**
     * string
     * color
     * integer
     * boolean
     *
     */
    private val valuesInterceptorMapper = HashMap<Int, String>()
    private val onResourceChangedListeners: HashSet<OnResourceChanged> = HashSet()
    internal var interceptedAsset: AssetManager? = null

    /**
     * 获取所有变更资源
     */
    fun getAllChangedResource(): Set<Int> = interceptorMapper

    /**
     * get changed value items
     */
    fun getAllValueChangedItem(): Map<Int, String> = valuesInterceptorMapper

    /**
     * add value xml item
     */
    fun addValuesInterceptor(id: Int, value: String) {
        valuesInterceptorMapper[id] = value
    }
    fun removeValue(id: Int) {
        valuesInterceptorMapper.remove(id)
    }


    /**
     * 新增拦截id
     */
    fun addInterceptor(type: String, value: Int) {
        interceptorMapper.add(value)
        when (type) {
            "layout" -> {
            }

            "drawable" -> {
                setApplyWhenCreate()
            }

            "color" -> {
                setApplyWhenCreate()
            }
        }
        ArrayList(onResourceChangedListeners).forEach {
            it.onResourceAdd(value)
        }
    }

    /**
     * 移除
     */
    fun removeInterceptor(value: Int) {
        if (interceptorMapper.remove(value)) {
            ArrayList(onResourceChangedListeners).forEach {
                it.onResourceRemove(value)
            }
            PackAssetsFile.deleteResource(ViewDebugInitializer.ctx, value)
        }
    }

    fun addResourceChangeListener(listener: OnResourceChanged) {
        onResourceChangedListeners.add(listener)
    }

    fun removeResourceChangeListener(listener: OnResourceChanged) {
        onResourceChangedListeners.remove(listener)
    }

    private fun setApplyWhenCreate() {
        // 如果没有开启创建即换肤，则开启，否则不生效，需要触发换肤才生效
        if (!SkinManager.isApplyWhenCreate()) {
            Logger.e("ViewDebugMergeResource", "applyWhenCreate has not open, now auto open")
            SkinManager.applyWhenCreate(true)

        }
    }


    interface OnResourceChanged {
        fun onResourceAdd(id: Int)
        fun onResourceRemove(id: Int)
    }
}