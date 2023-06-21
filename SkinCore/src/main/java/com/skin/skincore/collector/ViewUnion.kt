package com.skin.skincore.collector

import android.content.res.Configuration
import android.content.res.Resources
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.util.valueIterator
import com.example.skincore.R
import com.skin.skincore.SkinManager
import com.skin.skincore.parser.ParseOutValue

/**
 * view可换肤属性容器
 */
class ViewUnion(attrs: List<Attrs>? = null) : Iterable<Attrs> {
    // key 属性名称；value 属性值
    private val attrsMap = SparseArray<Attrs>()

    // 当前view的 app:skin 属性是什么
    var skinAttrValue: Int = ParseOutValue.SKIN_ATTR_UNDEFINE
        internal set

    // 从父布局那里继承而来的值
    var skinInheritedValue: Int = ParseOutValue.SKIN_ATTR_UNDEFINE
        internal set

    // 当前app:skin_forDescendants 属性是什么, 是否需要传递给后代
    var skinForDescendants: Int = ParseOutValue.SKIN_ATTR_UNDEFINE
        internal set

    init {
        attrs?.forEach {
            addAttr(it)
        }
    }

    fun addAttr(attr: Attrs) {
        attrsMap.put(attr.attributeId, attr)
        // attrsMap[attr.attributeId] = attr
    }

    fun addAttr(attrs: List<Attrs>) {
        attrs.forEach {
            addAttr(it)
        }
    }

    fun removeAttr(attributeId: Int) {
        attrsMap.remove(attributeId)
    }

    operator fun get(attributeId: Int): Attrs? {
        return attrsMap[attributeId]
    }

    override fun iterator(): Iterator<Attrs> = attrsMap.valueIterator()
}

// region view换肤快捷方法
/**
 * 获取View中的换肤属性
 */
fun View.getViewUnion(): ViewUnion? {
    return (this.getTag(R.id.view_sKinAttr) as? ViewUnion)
}

fun View.setViewUnion(union: ViewUnion) {
    this.setTag(R.id.view_sKinAttr, union)
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
fun View.addViewSkinAttrs(attrs: List<Attrs>): ViewUnion {
    var union = this.getViewUnion()
    if (union == null) {
        union = ViewUnion(attrs)
        this.setTag(R.id.view_sKinAttr, union)
    } else {
        union.addAttr(attrs)
    }
    return union
}

/**
 * 移除view的某些换肤属性，移除后，皮肤切换、白天黑夜切换该属性不再变化
 */
fun View.removeSkinAttr(attributeId: Int) {
    this.getViewUnion()?.removeAttr(attributeId)
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
        this.removeSkinAttr(android.R.attr.background)
    } else {
        this.setBackgroundResource(backgroundRes)
        this.addViewSkinAttrs(
            Attrs(
                backgroundRes,
                android.R.attr.background,
            ),
        )
    }
}

/**
 * ImageView代码设置图片
 */
fun ImageView.setImageResourceSkinAble(@DrawableRes resId: Int) {
    if (resId == 0) {
        this.setImageBitmap(null)
        this.removeSkinAttr(android.R.attr.src)
    } else {
        // 更新资源id和资源类型
        this.addViewSkinAttrs(
            Attrs(
                resId,
                android.R.attr.src,
            ),
        )
        val providedBitmap = SkinManager.getResourceProvider(this.context).getDrawable(resId, context.theme)
        this.setImageDrawable(providedBitmap)
    }
}

/**
 * TextView文本颜色换肤支持
 */
fun TextView.setTextColorSkinAble(@ColorRes resId: Int) {
    val resourceType = context.resources.getResourceTypeName(resId)
    // 文本颜色如果是drawable类型，那么一定是stateColor，不可能是图片
    if (resourceType == ResType.DRAWABLE) {
        val stateColor = SkinManager.getResourceProvider(this.context).getStateColor(resId, context.theme)
        this.setTextColor(stateColor)
    } else {
        val color = SkinManager.getResourceProvider(this.context).getColor(resId, context.theme)
        this.setTextColor(color)
    }
    this.addViewSkinAttrs(Attrs(resId, android.R.attr.textColor))
}

/**
 * 进度条progressDrawable支持
 */
fun ProgressBar.setProgressDrawableSkinAble(@DrawableRes resId: Int) {
    if (resId == 0) {
        progressDrawable = null
        this.removeSkinAttr(android.R.attr.progressDrawable)
    } else {
        progressDrawable = SkinManager.getResourceProvider(context).getDrawable(resId, null)
        this.addViewSkinAttrs(Attrs(resId, android.R.attr.progressDrawable))
    }
}

/**
 * 判断是否是夜间模式
 */
fun Resources.isNight(): Boolean {
    return configuration.isNight()
}

fun Configuration.isNight(): Boolean {
    return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

/**
 * 设置白天黑夜模式
 */
fun Resources.applyNight(isNight: Boolean) {
    val config = Configuration(configuration)
    config.applyNight(isNight)
    // 更新
    updateConfiguration(config, displayMetrics)
}

fun Configuration.applyNight(isNight: Boolean) {
    if (isNight) {
        uiMode = (
            Configuration.UI_MODE_NIGHT_YES or
                // 清空标志位
                (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
            )
    } else {
        uiMode = (
            Configuration.UI_MODE_NIGHT_NO
                or (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
            )
    }
}

// endregion
