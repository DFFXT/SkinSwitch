package com.cneeds.multipixelsuiadapter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.utils.ScreenUtils

/**
 * @Author : jyfstc
 * @Time : 2024/4/23 16:46
 * @Description :多分辨率布局适配
 */
class MultiPixelsAdjust {
    private var TAG = MultiPixelsAdjust::class.java.simpleName


    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private var currentUseUIConfig: UIConfig? = null
    var currentUseSuffix = ""
        private set

    private val screenRatioMap = HashMap<UIConfig, String>()
    // 比例计算器
    private var onNearestRatioCalculator: OnNearestRatioCalculator? = null
    private lateinit var application: Application
    fun init(context: Application) {
        application = context
    }

    /**
     * 设置比例计算器
     */
    fun setNearestRatioCalculator(calculator: OnNearestRatioCalculator) {
        onNearestRatioCalculator = calculator
    }

    /**
     * 添加多分辨率适配后缀
     * @param ratio 屏幕宽高比
     * @param suffix 资源后缀，一般以下划线开头：_small、_large、_vertical等
     * @param recalculate 是否重新计算，一般添加完比例数据后需要重新计算
     */
    fun addMultiPixelsSuffix(uiConfig: UIConfig, suffix: String, recalculate: Boolean = false) {
        screenRatioMap[uiConfig] = suffix
        if (recalculate) {
            updateConfiguration(application)
        }
    }

    /**
     * 当应用configuration变化时调用，用于更新参数
     */
    fun updateConfiguration(context: Context) {
        var displayMetrics = DisplayMetrics()
        try {
            // 判断是否是分屏模式，分屏模式下需要取分屏后的大小
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context is Activity && context.isInMultiWindowMode
                } else {
                    false
                }
            ) {
                // Launcher不会分屏，不影响
                displayMetrics = context.resources.displayMetrics
            } else {
                context.getSystemService(WindowManager::class.java).defaultDisplay.getRealMetrics(
                    displayMetrics
                )
            }
            screenWidth = displayMetrics.widthPixels.toFloat()
            screenHeight = displayMetrics.heightPixels.toFloat()
            currentUseUIConfig = null
            val calculator = onNearestRatioCalculator
            if (calculator == null) {
                // 默认使用最接近的比例
                val scale = screenWidth / screenHeight
                for (config in screenRatioMap.keys) {
                    val ratio = config.ratio
                    if (Math.abs(ratio - scale) < Math.abs((currentUseUIConfig?.ratio ?: Float.MAX_VALUE) - scale)) {
                        currentUseUIConfig = config
                    }
                }
            } else {
                // 使用外部的计算器来确定比例
                currentUseUIConfig = calculator.calculate()
            }

            currentUseSuffix = screenRatioMap[currentUseUIConfig] ?: ""

            // 找到接近的分辨率了，修改density等信息进行UI适配


            initAutoSizeConfig(context, currentUseUIConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initAutoSizeConfig(context: Context, uiConfig: UIConfig?) {
        // 此处需要手动设置屏幕宽高，防止分屏后宽高发生变化导致不一致
        val size = ScreenUtils.getScreenSize(context)
        AutoSizeConfig.getInstance().setScreenWidth(size[0])
        AutoSizeConfig.getInstance().setScreenHeight(size[1])

        AutoSizeConfig.getInstance()
            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启，上面3个fragment自定义适配就需要打开这个参数
            .setCustomFragment(true)
            .setDesignWidthInDp(uiConfig?.stdWidth ?: size[0])
            .setDesignHeightInDp(uiConfig?.stdHeight ?: size[1])
    }

    /**
     * 计算最接近的比率，用于决定使用哪个后缀
     */
    interface  OnNearestRatioCalculator {
        fun calculate():UIConfig
    }

    data class UIConfig(val stdWidth: Int, val stdHeight: Int) {
        val ratio = stdWidth / stdHeight.toFloat()
    }
}