package com.example.viewdebug.ui.page.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import com.example.viewdebug.R
import java.lang.ref.WeakReference
import java.util.LinkedList

class LayoutProfilerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val viewRects = LinkedList<ViewRect>()
    private val p = intArrayOf(0, 0)
    private val paint = Paint().apply {
        setColor(this@LayoutProfilerView.context.getColor(R.color.view_debug_red))
        style = Paint.Style.STROKE
    }

    private val linePaint = Paint().apply {
        setColor(Color.BLUE)
        isAntiAlias = true
        textSize = context.resources.getDimension(R.dimen.view_debug_min_text_size)
        // style = Paint.Style.STROKE
    }
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f

    private var highlightTarget: ViewRect? = null

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
            val parentRect = it.parent
            if (parentRect != null) {
                // left
                canvas.drawLine(it.left, it.getCenterY(), parentRect.left, it.getCenterY(), linePaint)
                canvas.drawText((it.left - parentRect.left).toString(), it.left - 100, it.getCenterY(), linePaint)
                // top
                canvas.drawLine(it.getCenterX(), it.top, it.getCenterX(), parentRect.top, linePaint)
                canvas.drawText((it.top - parentRect.top).toString(), it.getCenterX(), it.top, linePaint)
                // right
                canvas.drawLine(it.right, it.getCenterY(), parentRect.right, it.getCenterY(), linePaint)
                canvas.drawText((parentRect.right - it.right).toString(), it.right, it.getCenterY(), linePaint)
                // bottom
                canvas.drawLine(it.getCenterX(), it.bottom, it.getCenterX(), parentRect.bottom, linePaint)
                canvas.drawText((parentRect.bottom - it.bottom).toString(), it.getCenterX(), it.bottom + linePaint.textSize, linePaint)
            }
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
            return
        }
        target.getLocationOnScreen(p)
        val parent = target.parent as? View
        val parentRect = if (parent != null) {
            ViewRect(target.parent as View, null)
        } else null
        highlightTarget = ViewRect(target, parentRect)
        invalidate()
    }

    private class ViewRect(
        target: View,
        val parent: ViewRect? = null
    ) {
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val v= WeakReference(target)

        init {
            val p = intArrayOf(0, 0)
            target.getLocationOnScreen(p)
            left = p[0].toFloat()
            top = p[1].toFloat()
            right = p[0].toFloat() + target.measuredWidth
            bottom = p[1].toFloat() + target.measuredHeight
        }

        fun getCenterX() = (right + left) /2
        fun getCenterY() = (bottom + top) /2
    }


}