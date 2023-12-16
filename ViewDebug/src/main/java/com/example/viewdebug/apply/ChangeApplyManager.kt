package com.example.viewdebug.apply

import android.app.Application
import android.content.Context
import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.skin.log.Logger

object ChangeApplyManager {
    private val NAME = "ChangeApplyManager74396873"
    private val TAG = "ChangeApplyManager"
    private const val BUILD_KEY = "build_key"

    // 构建id，如果设置了构建id，那么dex则在当前id下生效，即使重启也会应用dex文件
    private lateinit var buildIdentification: IBuildIdentification

    /**
     * 是否加载本地更改
     * true：不是新版本|没有设置[IBuildIdentification]接口
     * false：新版本运行
     */
    private var applyLocalChange = false

    /**
     * 是否只应用一次
     */
    private var useOnce = true


    /**
     * 设置版本id接口
     * 如果设置了该接口，则重启应用后，如果[IBuildIdentification.getBuildId]的值不变则一直使用缓存的dex
     */
    fun init(context: Application, buildIdentification: IBuildIdentification?) {
        if (this::buildIdentification.isInitialized) return
        this.buildIdentification = buildIdentification ?: DefaultBuildIdentification()
        buildEnv(context)
        DexLoadManager.init(context, applyLocalChange, useOnce)
        XmlLoadManager.init(context, applyLocalChange, useOnce)
    }

    /**
     * @return true 可重复加载dex，false，dex只加载一次，然后就被删除
     */
    private fun buildEnv(context: Context) {
        val currentBuildId = buildIdentification.getBuildId()
        // 没有设置buildId，可以加载dex，但只允许加载一次

        when (currentBuildId) {
            IBuildIdentification.BuildType.BUILD_ID_CLEAR -> {
                applyLocalChange = false
                useOnce = true
            }
            IBuildIdentification.BuildType.BUILD_ID_HOLD -> {
                applyLocalChange = true
                useOnce = false
            }
            else -> {
                Logger.e(TAG, "buildEvn buildIdentification")
                applyLocalChange = true
                useOnce = true
            }
        }

    }

}