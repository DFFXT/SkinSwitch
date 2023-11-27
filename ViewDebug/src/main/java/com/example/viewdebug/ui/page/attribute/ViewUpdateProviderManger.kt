package com.example.viewdebug.ui.page.attribute

import android.view.View
import androidx.fragment.app.ViewDetailInfoDialog
import com.example.viewdebug.ui.page.attribute.impl.TextViewUpdateProvider
import java.util.LinkedList

internal class ViewUpdateProviderManger {
    private val providers = LinkedList<ViewExtraInfoProvider<*>>().apply {
        add(TextViewUpdateProvider())
    }

    private fun getProvider(view: View): ViewExtraInfoProvider<View>? {
        return providers.find { it.support(view) } as? ViewExtraInfoProvider<View>
    }

    fun getExtraInfo(view: View): List<ExtraInfo> {
        val list = LinkedList<ExtraInfo>()
        getProvider(view)?.extraInfoProvider?.forEach {
            if (it.value is Update) {
                list.add(
                    ExtraInfo(
                        ViewDetailInfoDialog.Item.TYPE_UPDATE,
                        it.key,
                        it.value.getValue(view),
                        it.value
                    )
                )
            } else {
                list.add(
                    ExtraInfo(
                        ViewDetailInfoDialog.Item.TYPE_COMMON,
                        it.key,
                        it.value.getValue(view),
                        it.value
                    )
                )
            }
        }

        return list
    }

    class ExtraInfo(val type: Int, val label: String, val value: String, val extra: Any? = null)
}