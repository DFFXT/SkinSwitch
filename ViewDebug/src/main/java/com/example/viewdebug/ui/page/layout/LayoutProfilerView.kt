package com.example.viewdebug.ui.page.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.DynamicLayout
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import androidx.core.text.set
import com.example.viewdebug.R
import java.lang.ref.WeakReference
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min

class LayoutProfilerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val viewRects = LinkedList<ViewRect>()
    private val p = intArrayOf(0, 0)
    private val paint = Paint().apply {
        setColor(this@LayoutProfilerView.context.getColor(R.color.view_debug_red))
        style = Paint.Style.STROKE
    }
    private val distanceTextColor = context.getColor(R.color.view_debug_distance_text_color)
    private val distanceTextBg = context.getColor(R.color.view_debug_distance_text_bg)
    private val distanceLineColor = context.getColor(R.color.view_debug_distance_line)
    private val linePaint = Paint().apply {
        setColor(distanceLineColor)
    }
    private val lineTextPaint = TextPaint().apply {
        setColor(distanceTextColor)
        isAntiAlias = true
        textSize = context.resources.getDimension(R.dimen.view_debug_small_text_size)
    }
    private val lineTextBgPaint = TextPaint().apply {
        setColor(distanceTextBg)
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
                drawText(canvas, it.distanceLeft, 0, it.distanceLeft.length, max(it.left - lineTextPaint.measureText(it.distanceLeft.toString()), 0f), it.getCenterY(), lineTextPaint)

                // top
                canvas.drawLine(it.getCenterX(), it.top, it.getCenterX(), parentRect.top, linePaint)
                drawText(canvas, it.distanceTop, 0, it.distanceTop.length, it.getCenterX(), max(it.top, lineTextPaint.textSize + offsetY), lineTextPaint)
                // right
                canvas.drawLine(it.right, it.getCenterY(), parentRect.right, it.getCenterY(), linePaint)
                drawText(canvas, it.distanceRight, 0, it.distanceRight.length, min(it.right, right - lineTextPaint.measureText(it.distanceRight.toString())), it.getCenterY(), lineTextPaint)
                // bottom
                canvas.drawLine(it.getCenterX(), it.bottom, it.getCenterX(), parentRect.bottom, linePaint)
                drawText(canvas, it.distanceBottom, 0, it.distanceBottom.length, it.getCenterX(), min(it.bottom + lineTextPaint.textSize + lineTextPaint.descent(), bottom.toFloat() + offsetY), lineTextPaint)
            }
        }
        canvas.translate(offsetX, offsetY)
    }

    private fun drawText(canvas: Canvas, text: CharSequence,s:Int,t:Int, x: Float, y:Float, paint: Paint) {
        canvas.drawRect(RectF(x, y - paint.textSize -paint.descent(), x + paint.measureText(text.toString()), y), lineTextBgPaint)
        canvas.drawText(text.toString(), x, y-paint.descent() , paint)
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

    private inner class ViewRect(
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
        val distanceLeft by lazy {
            parent ?: return@lazy SpannedString(null)
            getSpan((left - parent.left).toString())
        }
        val distanceTop by lazy {
            parent ?: return@lazy SpannableString(null)
            getSpan((top - parent.top).toString())
        }
        val distanceRight by lazy {
            parent ?: return@lazy SpannedString(null)
            getSpan((parent.right - right).toString())
        }
        val distanceBottom by lazy {
            parent ?: return@lazy SpannedString(null)
            getSpan((parent.bottom - bottom).toString())
        }
        private fun getSpan(text: String): SpannableString {
            return SpannableString(text).apply {
                set(0 ,this.length, BackgroundColorSpan(Color.RED))
            }
        }

        fun getCenterX() = (right + left) /2
        fun getCenterY() = (bottom + top) /2
    }


}