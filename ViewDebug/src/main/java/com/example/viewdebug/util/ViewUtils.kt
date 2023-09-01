package com.example.viewdebug.util

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.viewdebug.R
import java.lang.StringBuilder

internal fun View.setSize(width: Int? = null, height: Int? = null) {
    val lp = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    lp.width = width ?: lp.width
    lp.height = height ?: lp.height
    layoutParams = lp
}

internal fun View.enablePress() {
    foreground =
        AppCompatResources.getDrawable(context, R.drawable.view_debug_common_press_foreground)
}

internal fun View.enableSelect() {
    foreground =
        AppCompatResources.getDrawable(context, R.drawable.view_debug_common_selected_foreground)
    /*val d = StateListDrawable()
    val pd = AppCompatResources.getDrawable(context, drawableId)!!.mutate()
    pd.setTint(resources.getColor(R.color.view_debug_common_selected_color))
    d.addState(intArrayOf(android.R.attr.state_pressed), pd)
    val nd = AppCompatResources.getDrawable(context, drawableId)!!.mutate()
    d.addState(intArrayOf(), nd)
    background = d*/
}

class ViewDebugInfo(
    val layoutId: Int?,
    val invokeTrace: Array<StackTraceElement>
) {
    fun getLayoutName(res: Resources): String? {
        layoutId ?: return null
        if (layoutId > 0) {
            return res.getResourceEntryName(layoutId)
        }
        return null
    }

    fun getLayoutTypeAndName(res: Resources): String? {
        layoutId ?: return null
        if (layoutId > 0) {
            return res.getResourceTypeName(layoutId) + "/" + res.getResourceEntryName(layoutId)
        }
        return null
    }

    /**
     * 获取主要的几个调用关系
     */
    fun getMainInvokeTrace(): String {
        var anchor = false
        val builder = StringBuilder()
        for (ele in invokeTrace) {
            if (anchor) {
                builder.append(ele.className)
                builder.append(".")
                builder.append(ele.methodName)
                builder.append(":")
                builder.append(ele.lineNumber)
                builder.append("\n")
            }
            if (ele.methodName == "inflate" && ele.className == LayoutInflater::class.java.name) {
                anchor = true
            }
        }
        return builder.toString()
    }
}

internal fun View.getViewDebugInfo(): ViewDebugInfo? {
    return (this.getTag(R.id.view_debug_info) as? ViewDebugInfo)
}

internal fun View.setViewDebugInfo(info: ViewDebugInfo) {
    this.setTag(R.id.view_debug_info, info)
}

internal fun adjustOrientation(rootView: View) {
    val ctx = rootView.context
    if (ctx.resources.displayMetrics.widthPixels > ctx.resources.displayMetrics.heightPixels) {
        rootView.setSize(width = (ctx.resources.displayMetrics.widthPixels * 0.7).toInt())
        // rootView.minimumWidth = (ctx.resources.displayMetrics.widthPixels * 0.77f).toInt()
    } else {
        rootView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            matchConstraintMinWidth = (ctx.resources.displayMetrics.widthPixels * 0.8f).toInt()
        }
        rootView.minimumWidth = (ctx.resources.displayMetrics.widthPixels * 0.8f).toInt()
    }
}
