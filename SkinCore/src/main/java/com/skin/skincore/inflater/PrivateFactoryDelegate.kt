package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.tag.TAG_CREATE_VIEW

/**
 * LayoutInflater.mPrivateFactory的代理
 * 这里将inflater中的反射创建放在了这里
 */
class PrivateFactoryDelegate(
    private val inflater: LayoutInflater,
    viewCreated: IOnViewCreated,
    factory2: LayoutInflater.Factory2?
) : FactoryDelegate(viewCreated, factory2) {

    /**
     * 系统组件的前缀，复制于[com.android.internal.policy.PhoneLayoutInflater]
     */
    private val sClassPrefixList = arrayOf(
        "android.widget.",
        "android.webkit.",
        "android.app."
    )

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        Logger.i(TAG_CREATE_VIEW, "create private $name")
        var v = super.onCreateView(parent, name, context, attrs)
        if (v == null) {
            if (name.indexOf('.') == -1) {
                // 简写模式
                for (prefix in sClassPrefixList) {
                    try {
                        // 尝试创建view
                        v = inflater.createView(name, prefix, attrs)
                        if (v != null) {
                            break
                        }
                    } catch (_: Exception) {
                    }
                }
            } else {
                // 非简写模式
                v = inflater.createView(name, null, attrs)
            }
            if (v != null) {
                // super返回null，故没有走回调，这里兜底成功，创建了view，需回调
                viewCreated?.onViewCreated(v, name, attrs)
            }
        }
        Logger.i(TAG_CREATE_VIEW, "create private ${v != null}")
        return v
    }
}
