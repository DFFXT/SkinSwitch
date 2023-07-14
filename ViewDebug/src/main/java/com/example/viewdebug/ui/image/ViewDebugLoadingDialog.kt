package com.example.viewdebug.ui.image

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.viewdebug.R
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog

class ViewDebugLoadingDialog(host: UIPage) : BaseDialog(host) {
    override fun onCreateDialog(ctx: Context): View {
        return ProgressBar(ctx).apply {
            val size = ctx.resources.getDimensionPixelSize(R.dimen.view_debug_common_loading_size)
            layoutParams = ViewGroup.LayoutParams(size ,size)
        }
    }

    override fun clickClose(): Boolean = false

    override fun background(): Drawable = ColorDrawable(host.ctx.getColor(R.color.view_debug_dialog_background))
}