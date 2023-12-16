package com.example.viewdebug.apply

import androidx.annotation.IntDef
import androidx.annotation.StringDef


interface IBuildIdentification {
    enum class BuildType {
        // 只要返回这个id，则直接清空
        BUILD_ID_CLEAR,
        // 返回这个id则保留
        BUILD_ID_HOLD,
        // 只使用一次
        BUILD_ID_ONCE
    }
    /**
     * 获取当前构建id，如果id相同，则认为是同一个版本
     * 推荐在build.gradle中，使用buildConfigField在BuildConfig中添加构建字段，字段值为当前时间
     * 这样，每次构建其值都会不一样。
     * @return 见[BUILD_ID_CLEAR] [BUILD_ID_HOLD]
     */
    fun getBuildId(): BuildType

}