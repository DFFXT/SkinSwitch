package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

/**
 * 用于测试的资源提供器
 */
class TestResourceProvider : IResourceProvider {
    override fun getColor(resId: Int?): Int {
        return Color.RED
    }

    override fun getStateColor(resId: Int?): ColorStateList {
        return ColorStateList.valueOf(Color.RED)
    }

    override fun getDrawable(resId: Int?): Drawable {
        return ColorDrawable(Color.RED)
    }
}
