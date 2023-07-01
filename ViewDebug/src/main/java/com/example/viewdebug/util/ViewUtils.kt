package com.example.viewdebug.util

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.example.viewdebug.R

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

internal class ViewDebugInfo(
    val layoutId: Int?
)

internal fun View.getViewDebugInfo(): ViewDebugInfo? {
    return (this.getTag(R.id.view_debug_info) as? ViewDebugInfo)
}

internal fun View.setViewDebugInfo(info: ViewDebugInfo) {
    this.setTag(R.id.view_debug_info, info)
}
