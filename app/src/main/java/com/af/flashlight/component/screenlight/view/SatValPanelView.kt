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

class SatValPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val satPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val valPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rect = RectF()
    private val clipPath = Path()
    private val cornerRadius = 12f * resources.displayMetrics.density

    var hue: Float = 0f
        set(value) {
            field = value
            updateSatShader()
            invalidate()
        }

    var saturation: Float = 0f
        private set

    var value: Float = 1f
        private set

    var onSatValChanged: ((sat: Float, value: Float) -> Unit)? = null

    init {
        thumbPaint.style = Paint.Style.STROKE
        thumbPaint.strokeWidth = 3f * resources.displayMetrics.density
        thumbPaint.color = Color.WHITE

        thumbStrokePaint.style = Paint.Style.STROKE
        thumbStrokePaint.strokeWidth = 1.5f * resources.displayMetrics.density
        thumbStrokePaint.color = Color.DKGRAY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        clipPath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)

        val valShader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            Color.TRANSPARENT, Color.BLACK,
            Shader.TileMode.CLAMP
        )
        valPaint.shader = valShader
        updateSatShader()
    }

    private fun updateSatShader() {
        if (width <= 0) return
        val rgb = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            Color.WHITE, rgb,
            Shader.TileMode.CLAMP
        )
        satPaint.shader = satShader
    }

    fun setSatVal(sat: Float, v: Float, notify: Boolean = false) {
        saturation = sat.coerceIn(0f, 1f)
        value = v.coerceIn(0f, 1f)
        invalidate()
        if (notify) {
            onSatValChanged?.invoke(saturation, value)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(clipPath)

        canvas.drawRect(rect, satPaint)
        canvas.drawRect(rect, valPaint)

        canvas.restore()

        val cx = (saturation * width).coerceIn(0f, width.toFloat())
        val cy = ((1f - value) * height).coerceIn(0f, height.toFloat())
        val radius = 9f * resources.displayMetrics.density

        canvas.drawCircle(cx, cy, radius, thumbPaint)
        canvas.drawCircle(cx, cy, radius + 1.5f * resources.displayMetrics.density, thumbStrokePaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                val w = width.toFloat().coerceAtLeast(1f)
                val h = height.toFloat().coerceAtLeast(1f)
                saturation = (event.x / w).coerceIn(0f, 1f)
                value = (1f - (event.y / h)).coerceIn(0f, 1f)
                invalidate()
                onSatValChanged?.invoke(saturation, value)
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
