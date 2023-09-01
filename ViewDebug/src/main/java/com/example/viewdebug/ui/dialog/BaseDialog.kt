package com.example.viewdebug.ui.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.fxf.debugwindowlibaray.ui.UIPage

abstract class BaseDialog(protected val host: UIPage) {

    lateinit var parent: ViewGroup
    lateinit var dialogView: View
        private set

    var isShow: Boolean = false
        private set
    init {
        //create()
    }
    private fun create() {
        if (!this::dialogView.isInitialized) {
            parent = ConstraintLayout(host.ctx)
            dialogView = onCreateDialog(host.tabView.context, parent)
        }
    }

    abstract fun onCreateDialog(ctx: Context, parent: ViewGroup): View

    open fun show() {
        create()
        host.showDialog(this)
        isShow = true
    }

    open fun close() {
        if (isShow) {
            host.closeDialog(this)
        }
    }

    open fun onClose() {
        isShow = false
    }


    /**
     * 是否点击外部关闭
     */
    open fun clickClose(): Boolean = true

    open fun background(): Drawable? = null
}
