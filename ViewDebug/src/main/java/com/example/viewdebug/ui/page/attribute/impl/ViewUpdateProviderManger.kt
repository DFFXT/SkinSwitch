package com.example.viewdebug.ui.page.attribute.impl

import android.view.View
import com.example.viewdebug.ui.page.attribute.ViewUpdateProvider
import java.util.LinkedList

internal class ViewUpdateProviderManger {
    private val providers = LinkedList<ViewUpdateProvider<*>>().apply {
        add(TextViewUpdateProvider())
    }
    fun getProvider(view: View): ViewUpdateProvider<View>? {
        return providers.find { it.support(view) } as? ViewUpdateProvider<View>
    }
}