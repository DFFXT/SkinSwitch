package com.example.viewdebug.dex

import android.app.Application
import android.content.Context
import com.example.viewdebug.R
import com.example.viewdebug.dex.DexLoadManager.setBuildIdentification
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.makeAsDir
import com.example.viewdebug.util.shortToast
import com.skin.log.Logger
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import java.io.File

/**
 * 加载外部dex
 * 调用[setBuildIdentification]接口，实现dex在当前版本一直生效
 * [setBuildIdentification]接口需要在init之前调用
 */
object DexLoadManager {
    private const val TAG = "DexLoadManager"
    private const val NAME = "DexLoadManager74396873"
    private const val BUILD_KEY = "build_key"

    private lateinit var ctx: Application

    // 构建id，如果设置了构建id，那么dex则在当前id下生效，即使重启也会应用dex文件
    private var buildIdentification: IBuildIdentification? = null

    // 是否加载dex
    private var loadDex: Boolean = true

    // 文件接收路径
    private val receivePath by lazy {
        RemoteFileReceiver.getReceivePath(ctx).makeAsDir()
    }

    // dex临时存储路径
    private val dexTmpFolder by lazy {
        (RemoteFileReceiver.getBasePath(ctx) + "/dex-tmp").makeAsDir()
    }

    // dex永久存储路径
    private val dexFolder by lazy {
        (RemoteFileReceiver.getBasePath(ctx) + "/dex").makeAsDir()
    }

    fun init(context: Application) {
        this.ctx = context
        val useOld = buildEnv(context)
        RemoteFileReceiver.observe(object : RemoteFileReceiver.FileWatcher {
            override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
                if (fileInfo.type == RemoteFileReceiver.FileWatcher.TYPE_DEX) {
                    val originPath = fileInfo.originPath ?: return false
                    // 获取远程文件路径
                    val remoteFileName = File(originPath).name
                    launch(Dispatchers.Main) {
                        context.getString(R.string.view_debug_file_receive_tip, File(remoteFileName).name).shortToast()
                    }
                    // 返回false，走默认提示
                    return true
                }
                return false
            }
        })
        // 当前是重新build了，不能加载dex，如果没有实现IBuildIdentification那就没办法了，直接加载
        if (!loadDex) return
        val applyDexList = ArrayList<File>()
        receivePath.listFiles()?.forEach {
            if (it.name.endsWith(".dex")) {
                if (!useOld) {
                    // 如果只使用一次,不使用老的dex，则复制到dexTmp目录再使用
                    it.renameTo(File(dexTmpFolder, it.name))
                } else {
                    it.renameTo(File(dexFolder, it.name))
                }
            }
        }
        dexFolder.listFiles()?.forEach {
            if (it.name.endsWith(".dex")) {
                applyDexList.add(it)
            }
        }
        dexTmpFolder.listFiles()?.forEach {
            if (it.name.endsWith(".dex")) {
                applyDexList.add(it)
            }
        }
        if (applyDexList.isEmpty()) {
            Logger.i("DexLoadManager", "no dex file")
            return
        }
        Logger.i("DexLoadManager", "dex file: ${applyDexList.size}")
        // 有新的dex到来，直接加载
        applyDexLoad(context, applyDexList)

    }

    /**
     * 获取当前应用的dex文件名称
     */
    fun getAppliedDexList(): List<String> {
        val result = ArrayList<String>()
        result.addAll(dexFolder.list { dir, name -> name.endsWith(".dex") } ?: emptyArray())
        result.addAll(dexTmpFolder.list { dir, name -> name.endsWith(".dex") } ?: emptyArray())
        return result
    }

    /**
     * 设置版本id接口
     * 如果设置了该接口，则重启应用后，如果[IBuildIdentification.getBuildId]的值不变则一直使用缓存的dex
     */
    fun setBuildIdentification(buildIdentification: IBuildIdentification) {
        this.buildIdentification = buildIdentification
    }

    private fun applyDexLoad(context: Context, dexList: List<File>) {
        val cl = this.javaClass.classLoader!!
        // 获取旧的elements
        val oldElement = getElements(cl)
        // 获取新的elements
        val newElement: MutableList<Any> = ArrayList()
        dexList.forEach {
            val newLoader = DexClassLoader(it.absolutePath, context.getExternalFilesDir("")!!.absolutePath, null, cl)
            newElement.addAll(getElements(newLoader))
        }
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
            "已应用补丁".shortToast()
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


    /**
     * @return true 可重复加载dex，false，dex只加载一次，然后就被删除
     */
    private fun buildEnv(context: Context): Boolean {
        // 没有设置buildId，可以加载dex，但只允许加载一次
        if (buildIdentification == null) {
            Logger.e(TAG, "buildEvn buildIdentification")
            loadDex = true
            return false
        }
        val sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val currentBuildId = buildIdentification?.getBuildId()
        val storageBuildId = sp.getString(BUILD_KEY, null)

        // 1. 没有buildId，buildIdentification不为空
        // 2. 有buildId，id一直
        // 3. false
        val useOld: Boolean = if (buildIdentification != null && !sp.contains(BUILD_KEY)) {
            true
        } else {
            (currentBuildId != null && currentBuildId == storageBuildId)
        }

        // 如果当前有buildId，而且和本地存储不一致，则不加载dex
        loadDex = !(currentBuildId != null && currentBuildId != storageBuildId)

        buildIdentification?.let {
            sp.edit().putString(BUILD_KEY, currentBuildId).apply()
        }

        if (!useOld || !loadDex) {
            // 清除缓存
            dexFolder.deleteRecursively()
            dexTmpFolder.deleteRecursively()
        }
        return true
    }

}