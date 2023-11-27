package com.example.viewdebug.ui.page.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.viewdebug.R
import java.lang.ref.WeakReference
import java.util.LinkedList

class LayoutProfilerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val viewRects = LinkedList<ViewRect>()
    private val p = intArrayOf(0,0)
    private val paint = Paint().apply {
        setColor(this@LayoutProfilerView.context.getColor(R.color.view_debug_red))
        style = Paint.Style.STROKE
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
        }
        canvas.translate(offsetX, offsetY)
    }

    fun update(views: List<View>) {
        viewRects.clear()
        views.forEach {
            it.getLocationInWindow(p)
            viewRects.add(ViewRect(p[0].toFloat(), p[1].toFloat(), p[0].toFloat() + it.measuredWidth, p[1].toFloat() + it.measuredHeight, WeakReference(it)))
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
        highlightTarget = ViewRect(p[0].toFloat(), p[1].toFloat(), p[0].toFloat() + target.measuredWidth, p[1].toFloat() + target.measuredHeight, WeakReference(target))
        invalidate()
    }

    private class ViewRect(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val v: WeakReference<View>
    )


}