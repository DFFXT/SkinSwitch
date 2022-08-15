package com.skin.skincore

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import com.skin.skincore.inflater.SkinLayoutInflater

class AutoContext(base: Context) : ContextWrapper(base) {
    private val inflater = SkinLayoutInflater(LayoutInflater.from(base), this)
    override fun getSystemService(name: String): Any {
        return if (LAYOUT_INFLATER_SERVICE == name) {
            inflater
        } else {
            super.getSystemService(name)
        }
    }
}
