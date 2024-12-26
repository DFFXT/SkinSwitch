package com.skin.skincore.inflater

import android.util.AttributeSet
import android.view.View

interface IOnViewCreated {
    fun onViewCreated(parent: View?, view: View, name: String, attributeSet: AttributeSet)

    fun onInflateFinish(root: View)
}