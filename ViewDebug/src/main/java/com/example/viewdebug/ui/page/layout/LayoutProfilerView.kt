package com.example.viewdebug.ui.page.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugViewHighlightBinding
import java.lang.ref.WeakReference
import java.util.LinkedList

class LayoutProfilerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewRects = LinkedList<ViewRect>()
    private val p = intArrayOf(0, 0)
    private val paint = Paint().apply {
        setColor(this@LayoutProfilerView.context.getColor(R.color.view_debug_red))
        style = Paint.Style.STROKE
    }
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f

    private var highlightTarget: ViewRect? = null

    private val highlightBinding by lazy {
        val binding = ViewDebugViewHighlightBinding.inflate(LayoutInflater.from(context), this, true)
        binding.tvLeftDistance.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left < 0) {
                v.translationX = -left.toFloat()
            } else {
                v.translationX = 0f
            }
        }
        binding.tvTopDistance.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (top < 0) {
                v.translationY = -top.toFloat()
            } else {
                v.translationY = 0f
            }
        }
        binding.tvRightDistance.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (right > binding.root.right) {
                v.translationX = (binding.root.right - right).toFloat()
            } else {
                v.translationX = 0f
            }
            Log.e("tvRightDistance", v.translationX.toString())
        }
        binding.tvBottomDistance.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom > binding.root.bottom) {
                v.translationY = (binding.root.bottom - bottom).toFloat()
            } else {
                v.translationY = 0f
            }
        }
        binding
    }
    init {
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getLocationOnScreen(p)
        offsetX = p[0].toFloat()
        offsetY = p[1].toFloat()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(-offsetX, -offsetY)
        paint.style = Paint.Style.STROKE
        paint.alpha = 255
        viewRects.forEach {
            val v = it.v.get()
            if (v != null) {
                canvas.drawRect(it.left, it.top, it.right, it.bottom, paint)
            }
        }
        highlightTarget?.let {
            paint.style = Paint.Style.FILL
            paint.alpha = 100
            canvas.drawRect(it.left, it.top, it.right, it.bottom, paint)
        }
        canvas.translate(offsetX, offsetY)
    }

    fun update(views: List<View>) {
        viewRects.clear()
        views.forEach {
            it.getLocationOnScreen(p)
            viewRects.add(ViewRect(it))
        }
        invalidate()
    }

    fun highlight(target: View?) {
        target ?: run {
            highlightTarget = null
            invalidate()
            highlightBinding.root.isVisible = false
            return
        }
        val parent = target.parent as? View ?: return
        target.getLocationOnScreen(p)
        highlightBinding.root.isVisible = true
        highlightBinding.target.updateLayoutParams<MarginLayoutParams> {
            this.marginStart = (p[0] - offsetX).toInt()
            this.topMargin = (p[1] - offsetY).toInt()
            this.width = target.measuredWidth
            this.height = target.measuredHeight
        }
        parent.getLocationOnScreen(p)
        highlightBinding.targetParent.updateLayoutParams<MarginLayoutParams> {
            this.marginStart = (p[0] - offsetX).toInt()
            this.topMargin = (p[1] - offsetY).toInt()
            this.width = parent.measuredWidth
            this.height = parent.measuredHeight
        }
        highlightBinding.target.text = "${target.measuredWidth}*${target.measuredHeight}"
        highlightBinding.tvLeftDistance.text = (target.left).toString()
        highlightBinding.tvTopDistance.text = (target.top).toString()
        highlightBinding.tvRightDistance.text = (parent.right - target.right).toString()
        highlightBinding.tvBottomDistance.text = (parent.bottom - target.bottom).toString()
    }

    private class ViewRect(
        target: View
    ) {
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val v = WeakReference(target)


        init {
            val p = intArrayOf(0, 0)
            target.getLocationOnScreen(p)
            left = p[0].toFloat()
            top = p[1].toFloat()
            right = p[0].toFloat() + target.measuredWidth
            bottom = p[1].toFloat() + target.measuredHeight
        }

    }


}