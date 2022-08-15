package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

class SkinInflaterFactory(private val viewCreated: IOnViewCreated?, factory: LayoutInflater.Factory?) :
    LayoutInflater.Factory {

    private val otherFactory = ArrayList<LayoutInflater.Factory>()
    init {
        if (factory != null) {
            addFactory(factory)
        }
    }



    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        otherFactory.forEach {
            val v = it.onCreateView(name, context, attrs)
            if (v != null) {
                viewCreated?.onViewCreated(v, name, attrs)
                return v
            }
        }
        return null
    }

    fun addFactory(factory: LayoutInflater.Factory) {
        otherFactory.add(0, factory)
    }
}
