package com.example.viewdebug.ui.dialog

import android.content.Context
import android.view.View
import com.example.viewdebug.ui.UIPage

abstract class BaseDialog(protected val host: UIPage) {
    var dialogView: View
        private set

    init {
        dialogView = onCreateDialog(host.tabView.context)
    }

    abstract fun onCreateDialog(ctx: Context): View

    open fun show() {
        host.showDialog(dialogView)
    }

    open fun close() {
        host.closeDialog(dialogView)
    }
}
