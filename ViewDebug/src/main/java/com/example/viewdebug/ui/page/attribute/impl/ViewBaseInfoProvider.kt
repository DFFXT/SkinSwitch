package com.example.viewdebug.ui.page.attribute.impl

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannedString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentInfoRead
import com.example.viewdebug.server.RemoteControl
import com.example.viewdebug.server.ServerManager
import com.example.viewdebug.ui.page.PlaintTextDialog
import com.example.viewdebug.ui.page.attribute.Link
import com.example.viewdebug.ui.page.attribute.Read
import com.example.viewdebug.ui.page.attribute.ViewExtraInfoProvider
import com.example.viewdebug.util.getViewDebugInfo
import com.fxf.debugwindowlibaray.ui.UIPage

/**
 * view 基本信息提供
 */
internal class ViewBaseInfoProvider : ViewExtraInfoProvider<View>() {

    override val extraInfoProvider: LinkedHashMap<String, Read<View>> =
        LinkedHashMap<String, Read<View>>().apply {
            this["target"] = object : Read<View> {
                override fun getValue(view: View): String {
                    return view::class.java.simpleName
                }
            }
            this["id"] = object : Read<View> {
                override fun getValue(view: View): String? {
                    if (view.id > 0) {
                        return view.resources.getResourceName(view.id)
                    }
                    return null
                }
            }
            this["rect"] = object : Read<View> {
                override fun getValue(view: View): String {
                    val p = intArrayOf(0, 0)
                    view.getLocationOnScreen(p)
                    val wm = view.context.getSystemService(WindowManager::class.java)
                    return "${p[0]},${p[1]} - ${view.measuredWidth + p[0]}, ${view.measuredHeight + p[1]}"
                }
            }
            this["toParent"] = object : Read<View> {
                override fun getValue(view: View): String? {
                    val parent = view.parent as? ViewGroup
                    return if (parent != null) {
                        "${view.left},${view.top} ${parent.right - view.right},${parent.bottom - view.bottom}"
                    } else null
                }
            }
            this["size"] = object : Read<View> {
                override fun getValue(view: View): String {
                    return "${view.measuredWidth}*${view.measuredHeight}"
                }
            }
            this["padding"] = object : Read<View> {
                override fun getValue(view: View): String {
                    return "" + view.paddingStart + "," + view.paddingTop + "," + view.paddingEnd + "," + view.paddingBottom
                }
            }
            this["margin"] = object : Read<View> {
                override fun getValue(view: View): String {
                    return "" + view.marginStart + "," + view.marginTop + "," + view.marginEnd + "," + view.marginBottom
                }
            }

            this["SpecialParents"] = FragmentInfoRead()
            this["adapter"] = RecyclerAdapterReader()

            this["layout"] = object : Read<View> {
                override fun getValue(view: View): CharSequence? {
                    val showText = view.getViewDebugInfo()?.getLayoutTypeAndName(view.resources)
                    val layoutName = view.getViewDebugInfo()?.getLayoutName(view.resources)
                    if (layoutName != null && showText != null) {
                        val sps = SpannableString(showText)
                        sps.setSpan(object: ClickableSpan(){
                            override fun onClick(widget: View) {
                                RemoteControl.openXml("$layoutName.xml")
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                if (ServerManager.isConnected()) {
                                    super.updateDrawState(ds)
                                }
                            }

                        }, 0, showText.length, SpannedString.SPAN_INCLUSIVE_EXCLUSIVE)
                        return sps
                    }
                    return null
                }
            }
            this["layout-trace"] = object : Link<View> {
                var trace :String? = null
                override fun getValue(view: View): String {
                    trace = view.getViewDebugInfo()?.getMainInvokeTrace()
                    return "查看生成调用栈"
                }

                override fun onClick(host: UIPage) {
                    trace?.let { PlaintTextDialog(host).show(it) }
                }
            }
        }

    override fun support(view: View): Boolean {
        return true
    }
}