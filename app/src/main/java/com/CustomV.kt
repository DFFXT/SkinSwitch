package com

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.skin.skincore.SkinManager

class CustomV @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    init {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d("ssss", context.toString())
        val ctx = context.createPackageContext("com.baidu.carlifevehicle", Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
        SkinManager.makeContextSkinAble(ctx)
        val constructor = ctx.classLoader.loadClass("MyView").getDeclaredConstructor(Context::class.java)
        val v = constructor.newInstance(ctx) as View
        addView(v)
    }
}