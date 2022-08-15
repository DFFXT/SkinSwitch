package com.skin.skincore.inflater

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.skin.skincore.tag.Logger
import com.skin.skincore.tag.TAG_CREATE_VIEW

class PrivateInflaterFactory(
    private val inflater: SkinLayoutInflater,
    factory2: LayoutInflater.Factory2
) : SkinInflaterFactory2(inflater, factory2) {
    @SuppressLint("SoonBlockedPrivateApi")
    private val constructorArgsFiled =
        LayoutInflater::class.java.getDeclaredField("mConstructorArgs")
            .apply { isAccessible = true }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        Logger.logI(TAG_CREATE_VIEW, "create private $name")
        var v = super.onCreateView(parent, name, context, attrs)
        if (v == null) {
            if (name.indexOf('.') != -1) {
                val args = constructorArgsFiled.get(inflater) as Array<Any?>
                val isNullContext = args[0] == null
                if (isNullContext) {
                    args[0] = inflater.context
                }
                v = inflater.createView(name, null, attrs)
                inflater.onViewCreated(v, name, attrs)
            }
        }
        Logger.logI(TAG_CREATE_VIEW, "create private ${v != null}")
        return v
    }
}
