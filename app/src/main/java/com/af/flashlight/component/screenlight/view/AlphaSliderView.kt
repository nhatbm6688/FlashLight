package com.af.flashlight.component.screenlight.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
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

class AlphaSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val checkerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alphaPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val trackRect = RectF()
    private val clipPath = Path()
    private val cornerRadius = 8f * resources.displayMetrics.density

    var argbColor: Int = Color.MAGENTA
        set(value) {
            field = value
            updateAlphaShader()
            invalidate()
        }

    var alphaVal: Int = 255
        set(value) {
            field = value.coerceIn(0, 255)
            invalidate()
        }

    var onAlphaChanged: ((alpha: Int) -> Unit)? = null

    init {
        createCheckerPattern()
        thumbPaint.style = Paint.Style.FILL
        thumbBorderPaint.style = Paint.Style.STROKE
        thumbBorderPaint.strokeWidth = 2.5f * resources.displayMetrics.density
        thumbBorderPaint.color = Color.WHITE
    }

    private fun createCheckerPattern() {
        val size = (8f * resources.displayMetrics.density).toInt().coerceAtLeast(8)
        val bitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val p1 = Paint().apply { color = 0xFFE0E0E0.toInt() }
        val p2 = Paint().apply { color = 0xFFFFFFFF.toInt() }

        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), p1)
        canvas.drawRect(size.toFloat(), size.toFloat(), (size * 2).toFloat(), (size * 2).toFloat(), p1)
        canvas.drawRect(size.toFloat(), 0f, (size * 2).toFloat(), size.toFloat(), p2)
        canvas.drawRect(0f, size.toFloat(), size.toFloat(), (size * 2).toFloat(), p2)

        checkerPaint.shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        trackRect.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        clipPath.addRoundRect(trackRect, cornerRadius, cornerRadius, Path.Direction.CW)
        updateAlphaShader()
    }

    private fun updateAlphaShader() {
        if (width <= 0) return
        val r = Color.red(argbColor)
        val g = Color.green(argbColor)
        val b = Color.blue(argbColor)
        val start = Color.argb(0, r, g, b)
        val end = Color.argb(255, r, g, b)
        alphaPaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            start, end,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawRect(trackRect, checkerPaint)
        canvas.drawRect(trackRect, alphaPaint)
        canvas.restore()

        val cx = ((alphaVal / 255f) * width).coerceIn(0f, width.toFloat())
        val cy = height / 2f
        val radius = (height / 2f).coerceAtMost(10f * resources.displayMetrics.density)

        val r = Color.red(argbColor)
        val g = Color.green(argbColor)
        val b = Color.blue(argbColor)
        thumbPaint.color = Color.argb(alphaVal, r, g, b)
        canvas.drawCircle(cx, cy, radius, thumbPaint)
        canvas.drawCircle(cx, cy, radius, thumbBorderPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                val w = width.toFloat().coerceAtLeast(1f)
                alphaVal = ((event.x / w) * 255f).toInt().coerceIn(0, 255)
                invalidate()
                onAlphaChanged?.invoke(alphaVal)
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
