package com.skin.skincore.inflater

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.reflex.constructorArgsFiled
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
        "android.app.",
        "android.view."
    )

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        Logger.v(TAG_CREATE_VIEW, "create private $name")
        var v = super.onCreateView(parent, name, context, attrs)
        if (v == null) {
            if (name.indexOf('.') == -1) {
                // 简写模式
                for (prefix in sClassPrefixList) {
                    try {
                        // 尝试创建view
                        checkConstructorArgs()
                        v = inflater.createView(name, prefix, attrs)
                        if (v != null) {
                            break
                        }
                    } catch (_: Exception) {
                    }
                }
            } else {
                // 非简写模式
                checkConstructorArgs()
                v = inflater.createView(name, null, attrs)
            }
            if (v != null) {
                // super返回null，故没有走回调，这里兜底成功，创建了view，需回调
                viewCreated?.onViewCreated(parent, v, name, attrs)
            }
        }
        Logger.v(TAG_CREATE_VIEW, "create private ${v != null}")
        return v
    }

    /**
     * 检测构造方法的context参数是否存在，因为在factory中调用了inflater中的方法，导致参数异常，需要纠正
     * 实测在夜神模拟器7.1.2上参数会异常（只对args[1]赋值）, 之后不知道在哪个版本修复了这个问题，反正API32是不会异常（args[0]、args[1]均赋值了）
     */
    private fun checkConstructorArgs() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val args = constructorArgsFiled.get(inflater) as Array<Any?>
            if (args[0] == null) {
                args[0] = inflater.context
            }
        }
    }
}
