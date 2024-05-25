package com.example.viewdebug.ui.page.attribute

import android.view.View
import androidx.fragment.app.ViewDetailInfoDialog
import com.example.viewdebug.ui.page.attribute.impl.TextViewUpdateProvider
import com.example.viewdebug.ui.page.attribute.impl.ViewBaseInfoProvider
import com.example.viewdebug.xml.struct.reader.IRead
import java.util.LinkedList

internal object ViewInfoProviderManger {
    private val providers = LinkedList<ViewExtraInfoProvider<*>>().apply {
        add(ViewBaseInfoProvider())
        add(TextViewUpdateProvider())
    }

    private fun getProvider(view: View): List<ViewExtraInfoProvider<View>>? {
        return providers.filter { it.support(view) } as? List<ViewExtraInfoProvider<View>>
    }

    /**
     * 获取view的一些信息
     */
    fun getExtraInfo(view: View): List<ExtraInfo> {
        val list = LinkedList<ExtraInfo>()
        getProvider(view)?.forEach { provider ->
            provider.extraInfoProvider.forEach {
                val value = it.value.getValue(view)
                if (value != null) {
                    list.add(
                        ExtraInfo(
                            it.key,
                            value,
                            it.value
                        )
                    )
                }
            }
        }

        return list
    }

    class ExtraInfo(val label: String, val value: CharSequence, val extra: Read<View>? = null)
}