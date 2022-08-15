package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.skin.skincore.tag.Logger
import com.skin.skincore.tag.TAG_CREATE_VIEW

open class SkinInflaterFactory2(private val viewCreated: IOnViewCreated?, factory2: LayoutInflater.Factory2?) :
    LayoutInflater.Factory2 {
    private val otherFactory2 = ArrayList<LayoutInflater.Factory2>()
    init {
        if (factory2 != null) {
            addFactory(factory2)
        }
    }


    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        Log.i(TAG_CREATE_VIEW, "onCreateView4 $name")
        otherFactory2.forEach {
            val v = it.onCreateView(parent, name, context, attrs)
            if (v != null) {
                viewCreated?.onViewCreated(v, name, attrs)
                Logger.logI(TAG_CREATE_VIEW, "onCreateView4 Ok")
                return v
            }
        }
        Logger.logI(TAG_CREATE_VIEW, "onCreateView4 null")
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Log.i(TAG_CREATE_VIEW, "onCreateView3 $name")
        otherFactory2.forEach {
            val v = it.onCreateView(name, context, attrs)
            if (v != null) {
                viewCreated?.onViewCreated(v, name, attrs)
                Logger.logI(TAG_CREATE_VIEW, "onCreateView3 Ok")
                return v
            }
        }
        Logger.logI(TAG_CREATE_VIEW, "onCreateView3 null")
        return null
    }

    fun addFactory(factory2: LayoutInflater.Factory2) {
        otherFactory2.add(0, factory2)
    }
}
