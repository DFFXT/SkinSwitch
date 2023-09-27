package com.example.viewdebug.apply.dex

import android.app.Application
import android.content.Context
import com.example.viewdebug.apply.ModifyState
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.makeAsDir
import com.example.viewdebug.util.shortToast
import com.skin.log.Logger
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.io.File
import java.util.Collections

/**
 * 加载外部dex
 */
object DexLoadManager {
    private const val TAG = "DexLoadManager"

    private lateinit var ctx: Application

    // 文件接收路径
    private val receivePath by lazy {
        RemoteFileReceiver.getReceivePath(ctx).makeAsDir()
    }


    // dex永久存储路径
    private val dexFolder by lazy {
        (RemoteFileReceiver.getBasePath(ctx) + "/dex").makeAsDir()
    }

    private val dexMap = HashMap<String, DexInfo>()

    private var useOnce: Boolean = false

    /**
     * 初始化
     * @param loadDex 是否加载dex
     * @param useOnce 加载的dex是否只使用一次，true，使用后立即删除
     * 如果 [loadDex] == false, 则忽略[useOnce]
     */
    fun init(context: Application, loadDex: Boolean, useOnce: Boolean) {
        if (this::ctx.isInitialized) return
        this.useOnce = useOnce
        ctx = context

        // 当前是重新build了，不能加载dex，如果没有实现IBuildIdentification那就没办法了，直接加载
        if (!loadDex) {
            clear()
            return
        }
        val applyDexList = ArrayList<File>()
        dexMove()
        dexFolder.listFiles()?.forEach {
            if (it.name.endsWith(".dex")) {
                applyDexList.add(it)
            }
        }
        if (applyDexList.isEmpty()) {
            Logger.i(TAG, "no dex file")
            return
        }
        // 有新的dex到来，直接加载
        applyDexLoad(context, applyDexList, false)

        if (useOnce) {
            clear()
        }

    }

    fun hotApply() {
        applyDexLoad(ctx, dexMove(), true)
    }

    /**
     * 移动dex文件，根据是否只使用一次判断该移动到哪个文件夹
     * @return 移动了哪些文件
     */
    private fun dexMove(): List<File> {
        return receivePath.listFiles { _, name -> name.endsWith(".dex") }?.map {
            val dest = File(dexFolder, it.name)
            renameTo(it, dest)
            val extraFile = File(it.absolutePath + ".extra")
            renameTo(extraFile, File(dexFolder, extraFile.name))
            dest
        } ?: Collections.emptyList()
    }

    /**
     * 移动文件，先删除目标文件，因为renameTo无法覆盖
     */
    private fun renameTo(src: File, dest: File) {
        if (dest.exists()) {
            dest.delete()
        }
        src.renameTo(dest)
    }

    /**
     * 获取当前应用的dex文件名称
     * @return first: dex文件名称；second：是否已经应用
     */
    fun getAllDexList(): HashMap<String, DexInfo> {
        return dexMap
    }

    /**
     * 移除某个可应用dex
     * @param dexFileName 文件名称，非路径
     */
    fun removeAppliedDexList(dexFileName: String) {
        delete(dexFolder, dexFileName)
        dexMap.remove(dexFileName)
    }

    /**
     * 删除dir下的name文件
     */
    private fun delete(dir: File, name: String): Boolean {
        val dexDirDex = File(dir, name)
        if (dexDirDex.exists()) {
            dexDirDex.delete()
            return true
        }
        return false
    }

    /**
     * @param hotApply 是否是热加载（冷加载是启动时进行加载）
     */
    private fun applyDexLoad(context: Context, dexList: List<File>, hotApply: Boolean) {
        val cl = this.javaClass.classLoader!!
        // 获取旧的elements
        val oldElement = getElements(cl)
        // 获取新的elements
        val newElement: MutableList<Any> = ArrayList()
        dexList.forEach {
            val newLoader = DexClassLoader(it.absolutePath, context.getExternalFilesDir("")!!.absolutePath, null, cl)
            newElement.addAll(getElements(newLoader))
        }
        val arr =
            java.lang.reflect.Array.newInstance(oldElement.javaClass.componentType!!, oldElement.size + newElement.size)
        // 合并新旧, 新的在前，需要注意，这里要生成对应类型的数组
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
            "已应用补丁".shortToast()
        }
        dexList.forEach {
            val classList = getDexExtraInfo(it)
            val classMap = HashMap<String, ModifyState>()
            classList.forEach { clsName ->
                val isLoad = ClassLoadObserve.isLoaded(clsName)
                if (isLoad) {
                    classMap[clsName] = if (hotApply) ModifyState.REBOOT_UPDATABLE else ModifyState.INVALID_APPLY
                } else {
                    classMap[clsName] = ModifyState.APPLIED
                }
            }
            val info = DexInfo(it.name, classMap)
            dexMap[info.name] = info
        }
    }

    private fun getDexExtraInfo(dexFile: File): List<String> {
        val list = ArrayList<String>()
        val extraFile = File(dexFile.absolutePath + ".extra")
        if (extraFile.exists()) {
            val json = JSONObject(extraFile.readText())
            val arr = json.getJSONArray("class")

            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        }
        return list
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


    fun clear() {
        // 清除缓存
        dexFolder.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

    class DexInfo(val name: String, val classList: HashMap<String, ModifyState>) {
        fun getModifyState(): ModifyState {
            return classList.maxByOrNull { it.value.ordinal }?.value ?: ModifyState.UNKNOWN
        }
    }


}