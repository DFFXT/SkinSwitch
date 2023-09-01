package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugImageDetailBinding
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.util.adjustOrientation
import com.skin.skincore.collector.setBackgroundResourceSkinAble
import com.skin.skincore.collector.setImageResourceSkinAble

/**
 * 图片详情
 */
internal class ImageDetailDialog(host: UIPage) : BaseDialog(host) {

    companion object {
        private var selectedIndex = 0
    }

    private lateinit var binding: ViewDebugImageDetailBinding

    // 当前选择的背景sample对象
    private var currentSelectedView: View? = null
    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        binding = ViewDebugImageDetailBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.root.apply {
            val width = ctx.resources.displayMetrics.widthPixels
            val height = ctx.resources.displayMetrics.heightPixels
            layoutParams = ViewGroup.LayoutParams(width / 3, height / 3)
        }
        binding.color1.setOnClickListener {
            updateSelectedBg(binding.color1, 0)
        }
        binding.color2.setOnClickListener {
            updateSelectedBg(binding.color2, 1)
        }
        if (selectedIndex == 0) {
            updateSelectedBg(binding.color1, 0)
        } else {
            updateSelectedBg(binding.color2, 1)
        }
        adjustOrientation(binding.root)
        return binding.root
    }

    fun show(resId: Int) {
        show()
        val drawable = AppCompatResources.getDrawable(binding.root.context, resId) ?: return
        if (drawable.intrinsicWidth > 0) {
            binding.ivImage.setImageResourceSkinAble(resId)
            binding.ivImage.setBackgroundResourceSkinAble(0)
        } else {
            binding.ivImage.setBackgroundResourceSkinAble(resId)
            binding.ivImage.setImageResourceSkinAble(0)
        }

    }

    private fun updateSelectedBg(view: View, index: Int) {
        if (view == currentSelectedView) return
        currentSelectedView?.let {
            it.foreground = null
            scaleTo(it, 1f)
        }
        view.foreground = AppCompatResources.getDrawable(view.context, R.drawable.view_debug_circle_stroke_1dp)
        scaleTo(view, 1.2f)
        binding.root.setBackgroundColor(view.backgroundTintList?.defaultColor ?: 0)
        currentSelectedView = view
        selectedIndex = index
    }

    private fun scaleTo(view: View, scaleTo: Float) {
        view.animate().scaleX(scaleTo).scaleY(scaleTo).setDuration(300).start()
    }
}