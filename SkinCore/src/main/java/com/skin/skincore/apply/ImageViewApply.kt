package com.skin.skincore.apply

import android.view.View
import android.widget.ImageView
import com.skin.skincore.collector.DefaultAttrCollector
import com.skin.skincore.provider.IResourceProvider

class ImageViewApply : ViewApply<ImageView>() {
    override fun apply(view: View) = view is ImageView
    private fun applySrc(
        view: ImageView,
        customResType: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        // 颜色、图片都可以转化为drawable
        view.setImageDrawable(provider.getDrawable(resId))
    }

    override fun customApply(
        view: ImageView,
        resType: String,
        attrName: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (attrName) {
            DefaultAttrCollector.ATTR_SRC -> {
                applySrc(view, resType, provider, resId)
            }
            else -> super.customApply(view, resType, attrName, provider, resId)
        }
    }
}
