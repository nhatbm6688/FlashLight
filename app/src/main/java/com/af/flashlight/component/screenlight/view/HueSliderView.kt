package com.af.flashlight.component.screenlight.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class HueSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val trackRect = RectF()
    private val clipPath = Path()
    private val cornerRadius = 8f * resources.displayMetrics.density

    var hue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 360f)
            invalidate()
        }

    var onHueChanged: ((hue: Float) -> Unit)? = null

    init {
        thumbPaint.style = Paint.Style.FILL
        thumbBorderPaint.style = Paint.Style.STROKE
        thumbBorderPaint.strokeWidth = 2.5f * resources.displayMetrics.density
        thumbBorderPaint.color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        trackRect.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        clipPath.addRoundRect(trackRect, cornerRadius, cornerRadius, Path.Direction.CW)

        val colors = intArrayOf(
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA,
            Color.RED
        )
        val shader = LinearGradient(
            0f, 0f, w.toFloat(), 0f,
            colors, null,
            Shader.TileMode.CLAMP
        )
        trackPaint.shader = shader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawRect(trackRect, trackPaint)
        canvas.restore()

        val cx = ((hue / 360f) * width).coerceIn(0f, width.toFloat())
        val cy = height / 2f
        val radius = (height / 2f).coerceAtMost(10f * resources.displayMetrics.density)

        thumbPaint.color = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        canvas.drawCircle(cx, cy, radius, thumbPaint)
        canvas.drawCircle(cx, cy, radius, thumbBorderPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                val w = width.toFloat().coerceAtLeast(1f)
                hue = ((event.x / w) * 360f).coerceIn(0f, 360f)
                invalidate()
                onHueChanged?.invoke(hue)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
