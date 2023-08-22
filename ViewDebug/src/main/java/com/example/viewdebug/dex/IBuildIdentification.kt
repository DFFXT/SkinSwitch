package com.example.viewdebug.dex


interface IBuildIdentification {
    /**
     * 获取当前构建id，如果id相同，则认为是同一个版本
     * 推荐在build.gradle中，使用buildConfigField在BuildConfig中添加构建字段，字段值为当前时间
     * 这样，每次构建其值都会不一样。
     */
    fun getBuildId(): String
}