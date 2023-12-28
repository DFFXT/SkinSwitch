package com

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.example.skinswitch.R

/**
 * 自定义属性换肤
 */
class CustomViewTestSkinView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        context.obtainStyledAttributes(R.styleable.CustomViewTestSkinView).use {
            if (it.hasValue(R.styleable.CustomViewTestSkinView_custom_bg)) {
                //setCustomBg(AppCompatResources.getDrawable(context, it.getResourceId(R.styleable.CustomViewTestSkinView_custom_bg, 0)))
                setCustomBg(it.getDrawable(R.styleable.CustomViewTestSkinView_custom_bg))
            }
        }
    }

    fun setCustomBg(drawable: Drawable?) {
        background = drawable
    }
}