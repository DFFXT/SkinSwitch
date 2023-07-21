package com.example.viewdebug.dex

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import java.io.File

/**
 * 加载外部dex
 */
class DexLoadManager{
    // 接收文件地址
    private val dexPath = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + "/view-debug/receive/view-debug.dex"
    // 重命名地址
    private val renamePath = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + "/view-debug/view-debug-apply.dex"
    fun init() {
        RemoteFileReceiver.observe(object : RemoteFileReceiver.FileWatcher {
            override fun onChange(path: String): Boolean {
                if (path == dexPath) {
                    // 提示是否加载
                    return true
                }
                return false
            }
        })
        val rowDexPath = File(dexPath)
        if (!rowDexPath.exists()) {
            return
        }
        // 对dex进行重命名，防止重复加载
        rowDexPath.renameTo(File(renamePath))
        // 有新的dex到来，直接加载
        applyDexLoad()

    }

    private fun applyDexLoad() {
        val cl = this.javaClass.classLoader!!
        // 获取旧的elements
        val oldElement = getElements(cl)
        val newLoader = DexClassLoader(renamePath, ViewDebugInitializer.ctx.getExternalFilesDir("")!!.absolutePath, null, cl)
        // 获取新的elements
        val newElement = getElements(newLoader)
        val arr = java.lang.reflect.Array.newInstance(oldElement.javaClass.componentType!!, oldElement.size + newElement.size)
        // 合并新旧，需要注意，这里要生成对应类型的数组
        repeat(oldElement.size + newElement.size) {

            val value = if (it < newElement.size) {
                newElement[it]
            } else {
                oldElement[it - newElement.size]
            }
            java.lang.reflect.Array.set(arr, it, value)
        }
        setElements(cl, arr)
        launch(Dispatchers.Main) {
            "已应用补9丁".shortToast()
        }
    }


    private fun getElements(classLoader: ClassLoader): Array<Any> {
        val pathListFiled = (BaseDexClassLoader::class.java as Class).getDeclaredField("pathList")
        pathListFiled.isAccessible = true
        val pathList = pathListFiled.get(classLoader)
        val dexElementsFiled = pathList::class.java.getDeclaredField("dexElements")
        dexElementsFiled.isAccessible = true
        return dexElementsFiled.get(pathList) as Array<Any>
    }

    private fun setElements(classLoader: ClassLoader, element: Any) {
        val pathListFiled = (BaseDexClassLoader::class.java as Class).getDeclaredField("pathList")
        pathListFiled.isAccessible = true
        val pathList = pathListFiled.get(classLoader)
        val dexElementsFiled = pathList::class.java.getDeclaredField("dexElements")
        dexElementsFiled.isAccessible = true
        return dexElementsFiled.set(pathList, element)
    }

}