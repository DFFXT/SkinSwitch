package com.skin.skincore.collector

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.skincore.R
import com.skin.log.Logger
import com.skin.skincore.SkinManager

/**
 * view可换肤属性容器
 */
class ViewUnion(attrs: List<Attrs>? = null) : Iterable<Map.Entry<String, Attrs>> {
    // key 属性名称；value 属性值
    private val attrsMap = HashMap<String, Attrs>()

    init {
        attrs?.forEach {
            addAttr(it)
        }
    }

    fun addAttr(attr: Attrs) {
        attrsMap[attr.attributeName] = attr
    }

    fun addAttr(attrs: List<Attrs>) {
        attrs.forEach {
            addAttr(it)
        }
    }

    fun removeAttr(attributeName: String) {
        attrsMap.remove(attributeName)
    }

    override fun iterator(): Iterator<Map.Entry<String, Attrs>> = attrsMap.iterator()
}

// region view换肤快捷方法
/**
 * 获取View中的换肤属性
 */
fun View.getViewUnion(): ViewUnion? {
    return (this.getTag(R.id.view_sKinAttr) as? ViewUnion)
}

/**
 * 添加换肤属性
 */
fun View.addViewSkinAttrs(attr: Attrs) {
    var union = this.getViewUnion()
    if (union == null) {
        union = ViewUnion().apply {
            addAttr(attr)
        }
        this.setTag(R.id.view_sKinAttr, union)
    } else {
        union.addAttr(attr)
    }
}

/**
 * 给View添加可换肤的属性
 */
fun View.addViewSkinAttrs(attrs: List<Attrs>) {
    var union = this.getViewUnion()
    if (union == null) {
        union = ViewUnion(attrs)
        this.setTag(R.id.view_sKinAttr, union)
    } else {
        union.addAttr(attrs)
    }
}

/**
 * 移除view的某些换肤属性，移除后，皮肤切换、白天黑夜切换该属性不再变化
 * @param attributeName 属性名称，见[DefaultCollector.ATTR_BACKGROUND]或者其他自定义支持的属性
 */
fun View.removeSkinAttr(attributeName: String) {
    this.getViewUnion()?.removeAttr(attributeName)
}

/**
 * 清空该view的换肤能力，外部如果想清除，需调用[SkinManager.removeView]
 */
internal fun View.clearSkinAttr() {
    this.setTag(R.id.view_sKinAttr, null)
}

// endregion

// region 代码更改资源
/**
 * 设置背景，如果为0则清除当前view的背景
 */
fun View.setBackgroundResourceSkinAble(@DrawableRes backgroundRes: Int) {
    if (backgroundRes == 0) {
        background = null
    } else {
        this.setBackgroundResource(backgroundRes)
        this.addViewSkinAttrs(
            Attrs(
                backgroundRes,
                DefaultCollector.ATTR_BACKGROUND,
                context.resources.getResourceTypeName(backgroundRes)
            )
        )
    }
}

/**
 * ImageView代码设置图片
 */
fun ImageView.setImageResourceSkinAble(@DrawableRes resId: Int) {
    if (resId == 0) {
        this.setImageBitmap(null)
    } else {
        // 更新资源id和资源类型
        this.addViewSkinAttrs(
            Attrs(
                resId,
                DefaultCollector.ATTR_SRC,
                context.resources.getResourceTypeName(resId)
            )
        )
        val providedBitmap = SkinManager.getResourceProvider(this.context).getDrawable(resId)
        this.setImageDrawable(providedBitmap)
    }
}

/**
 * TextView文本颜色换肤支持
 */
fun TextView.setTextColorSkinAble(@ColorRes resId: Int) {
    var resourceType = context.resources.getResourceTypeName(resId)
    // 文本颜色如果是drawable类型，那么一定是stateColor，不可能是图片
    if (resourceType == Attrs.DRAWABLE) {
        resourceType = Attrs.STATE_COLOR
        val stateColor = SkinManager.getResourceProvider(this.context).getStateColor(resId)
        this.setTextColor(stateColor)
    } else {
        val color = SkinManager.getResourceProvider(this.context).getColor(resId)
        if (color != null) {
            this.setTextColor(color)
        } else {
            Logger.d("setTextColorSkinAble", "get text color for id: $resId failed")
        }
    }
    this.addViewSkinAttrs(Attrs(resId, DefaultCollector.ATTR_TEXT_COLOR, resourceType))
}

// endregion
