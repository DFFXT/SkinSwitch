package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.tag.TAG_CREATE_VIEW

/**
 * 代理类，代理Factory和Factory2
 */
open class FactoryDelegate(
    protected val viewCreated: IOnViewCreated?,
    factory: LayoutInflater.Factory?
) :
    Factory2 {
    private val otherFactory = ArrayList<LayoutInflater.Factory>()

    init {
        if (factory != null) {
            addFactory(factory)
        }
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        Logger.v(TAG_CREATE_VIEW, "onCreateView4 $name")
        otherFactory.forEach {
            if (it is Factory2) {
                val v = it.onCreateView(parent, name, context, attrs)
                if (v != null) {
                    viewCreated?.onViewCreated(parent, v, name, attrs)
                    Logger.v(TAG_CREATE_VIEW, "onCreateView4 Ok")
                    return v
                }
            }
        }
        Logger.v(TAG_CREATE_VIEW, "onCreateView4 null")
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Log.i(TAG_CREATE_VIEW, "onCreateView3 $name")
        otherFactory.forEach {
            val v = it.onCreateView(name, context, attrs)
            if (v != null) {
                viewCreated?.onViewCreated(null, v, name, attrs)
                Logger.v(TAG_CREATE_VIEW, "onCreateView3 Ok")
                return v
            }
        }
        Logger.v(TAG_CREATE_VIEW, "onCreateView3 null")
        return null
    }

    /**
     * 添加被代理的Factory
     */
    fun addFactory(factory: LayoutInflater.Factory) {
        if (factory is FactoryDelegate) {
            // 已经被代理过了，则取里面的被代理类，防止多重代理，产生多次回调
            otherFactory.addAll(0, factory.otherFactory)
        } else {
            otherFactory.add(0, factory)
        }
    }
}
