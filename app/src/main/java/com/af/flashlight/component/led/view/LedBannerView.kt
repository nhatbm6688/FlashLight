package com.af.flashlight.component.led.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.af.flashlight.R
import com.af.flashlight.component.led.model.LedDirection
import com.af.flashlight.component.led.model.LedEffect
import kotlin.math.sin

class LedBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var text: String = "HELLO WORLD"
        set(value) {
            field = if (value.isBlank()) " " else value
            measureTextBounds()
            resetPosition()
            invalidate()
        }

    var ledColor: Int = Color.parseColor("#FFD54F")
        set(value) {
            field = value
            updatePaints()
            invalidate()
        }

    var fontSizeSp: Float = 48f
        set(value) {
            field = value.coerceIn(16f, 160f)
            updateTextSize()
            measureTextBounds()
            resetPosition()
            invalidate()
        }

    var speedLevel: Int = 5 // 1 (slowest) to 10 (fastest)
        set(value) {
            field = value.coerceIn(1, 10)
        }

    var direction: LedDirection = LedDirection.LEFT
        set(value) {
            field = value
            resetPosition()
            invalidate()
        }

    var effect: LedEffect = LedEffect.GLOW
        set(value) {
            field = value
            invalidate()
        }

    private var bgBitmap: Bitmap? = null
    private val bgMatrix = Matrix()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlayPaint = Paint().apply {
        color = Color.argb(90, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val textBounds = Rect()
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    private var posX: Float = 0f
    private var posY: Float = 0f
    private var lastFrameTime: Long = 0L
    private var isAnimating: Boolean = false

    private val customTypeface: Typeface? by lazy {
        try {
            ResourcesCompat.getFont(context, R.font.plus_jakarta_sans_bold)
                ?: Typeface.DEFAULT_BOLD
        } catch (_: Exception) {
            Typeface.DEFAULT_BOLD
        }
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        textPaint.typeface = customTypeface
        glowPaint.typeface = customTypeface
        updatePaints()
        updateTextSize()
    }

    override fun setBackgroundResource(resId: Int) {
        if (resId == 0) {
            bgBitmap = null
        } else {
            try {
                val raw = BitmapFactory.decodeResource(resources, resId)
                bgBitmap = processAndRotateBitmap(raw)
            } catch (_: Exception) {
                bgBitmap = null
            }
        }
        invalidate()
    }

    fun setBackgroundUri(uri: Uri?) {
        if (uri == null) {
            bgBitmap = null
        } else {
            bgBitmap = loadBitmapFromUri(uri)
        }
        invalidate()
    }

    private fun processAndRotateBitmap(original: Bitmap?): Bitmap? {
        if (original == null) return null
        // Nếu ảnh là dạng dọc (portrait: height > width), tự động xoay 90 độ sang ngang (landscape)
        // để khớp với khung màn hình LED, chống méo tỉ lệ và vỡ hình
        return if (original.height > original.width) {
            val matrix = Matrix().apply { postRotate(90f) }
            try {
                val rotated = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
                if (rotated != original) {
                    original.recycle()
                }
                rotated
            } catch (_: Exception) {
                original
            }
        } else {
            original
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            var exifRotation = 0
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                exifRotation = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }

            // Downsampling để tránh OutOfMemory trên các máy chụp ảnh 50MP
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            var sampleSize = 1
            val maxDim = 2048
            while (options.outWidth / (sampleSize * 2) >= maxDim || options.outHeight / (sampleSize * 2) >= maxDim) {
                sampleSize *= 2
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = sampleSize
            val decoded = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return null

            // Áp dụng xoay EXIF trước (nếu có)
            var orientedBmp = decoded
            if (exifRotation != 0) {
                val matrix = Matrix().apply { postRotate(exifRotation.toFloat()) }
                val rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
                if (rotated != decoded) {
                    decoded.recycle()
                }
                orientedBmp = rotated
            }

            // Tự động xoay 90 độ nếu ảnh vẫn là ảnh dọc để khớp với khung LED ngang
            if (orientedBmp.height > orientedBmp.width) {
                val matrix = Matrix().apply { postRotate(90f) }
                val rotated = Bitmap.createBitmap(orientedBmp, 0, 0, orientedBmp.width, orientedBmp.height, matrix, true)
                if (rotated != orientedBmp) {
                    orientedBmp.recycle()
                }
                orientedBmp = rotated
            }

            orientedBmp
        } catch (e: Exception) {
            null
        }
    }

    private fun updatePaints() {
        textPaint.color = ledColor

        val r = Color.red(ledColor)
        val g = Color.green(ledColor)
        val b = Color.blue(ledColor)
        val glowColor = Color.argb(220, r, g, b)

        glowPaint.color = ledColor
        glowPaint.setShadowLayer(28f, 0f, 0f, glowColor)
    }

    private fun updateTextSize() {
        val px = fontSizeSp * resources.displayMetrics.scaledDensity
        textPaint.textSize = px
        glowPaint.textSize = px
    }

    private fun measureTextBounds() {
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        textWidth = textPaint.measureText(text)
        val fontMetrics = textPaint.fontMetrics
        textHeight = fontMetrics.descent - fontMetrics.ascent
    }

    private fun resetPosition() {
        if (width == 0 || height == 0) return
        val w = width.toFloat()
        val h = height.toFloat()

        when (direction) {
            LedDirection.LEFT -> {
                posX = w + textWidth / 2f
                posY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
            }
            LedDirection.RIGHT -> {
                posX = -textWidth / 2f
                posY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
            }
            LedDirection.UP -> {
                posX = w / 2f
                posY = h + textHeight
            }
            LedDirection.DOWN -> {
                posX = w / 2f
                posY = -textHeight
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        measureTextBounds()
        resetPosition()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    fun startAnimation() {
        if (!isAnimating) {
            isAnimating = true
            lastFrameTime = SystemClock.uptimeMillis()
            postInvalidateOnAnimation()
        }
    }

    fun stopAnimation() {
        isAnimating = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // 1. Draw background with Center-Crop Matrix (no stretch/distortion)
        bgBitmap?.let { bmp ->
            val bmpW = bmp.width.toFloat()
            val bmpH = bmp.height.toFloat()

            val scaleX = w / bmpW
            val scaleY = h / bmpH
            val scale = maxOf(scaleX, scaleY)

            val scaledW = bmpW * scale
            val scaledH = bmpH * scale
            val dx = (w - scaledW) / 2f
            val dy = (h - scaledH) / 2f

            bgMatrix.reset()
            bgMatrix.postScale(scale, scale)
            bgMatrix.postTranslate(dx, dy)

            canvas.drawBitmap(bmp, bgMatrix, bgPaint)
            canvas.drawRect(0f, 0f, w, h, overlayPaint)
        } ?: run {
            canvas.drawColor(Color.BLACK)
        }

        val currentTime = SystemClock.uptimeMillis()
        val dt = if (lastFrameTime == 0L) 16L else (currentTime - lastFrameTime).coerceIn(1L, 100L)
        lastFrameTime = currentTime

        // 2. Calculate animation movement based on speed
        // Base speed: 60dp/sec at level 1, up to 600dp/sec at level 10
        val density = resources.displayMetrics.density
        val baseSpeedPx = (50f + speedLevel * 45f) * density
        val deltaMovement = baseSpeedPx * (dt / 1000f)

        when (direction) {
            LedDirection.LEFT -> {
                posX -= deltaMovement
                if (posX < -textWidth / 2f) {
                    posX = w + textWidth / 2f
                }
            }
            LedDirection.RIGHT -> {
                posX += deltaMovement
                if (posX > w + textWidth / 2f) {
                    posX = -textWidth / 2f
                }
            }
            LedDirection.UP -> {
                posY -= deltaMovement
                if (posY < -textHeight) {
                    posY = h + textHeight
                }
            }
            LedDirection.DOWN -> {
                posY += deltaMovement
                if (posY > h + textHeight) {
                    posY = -textHeight
                }
            }
        }

        // 3. Compute Effect (Glow, Blink, Neon, Fade)
        val originalAlpha = Color.alpha(ledColor)
        var currentAlpha = originalAlpha

        when (effect) {
            LedEffect.GLOW -> {
                currentAlpha = originalAlpha
                glowPaint.setShadowLayer(32f, 0f, 0f, ledColor)
            }
            LedEffect.BLINK -> {
                // Blink cycle ~ 600ms (300ms on, 300ms off)
                val cycle = (currentTime % 600L) < 300L
                currentAlpha = if (cycle) originalAlpha else 0
            }
            LedEffect.NEON -> {
                // Subtle flicker / breathing glow
                val phase = sin(currentTime / 180.0).toFloat() // -1..1
                val shadowRadius = 24f + phase * 10f
                val glowAlpha = (180 + (phase * 60).toInt()).coerceIn(60, 255)
                val r = Color.red(ledColor)
                val g = Color.green(ledColor)
                val b = Color.blue(ledColor)
                glowPaint.setShadowLayer(shadowRadius, 0f, 0f, Color.argb(glowAlpha, r, g, b))
                currentAlpha = (originalAlpha * (0.85f + 0.15f * phase)).toInt().coerceIn(0, 255)
            }
            LedEffect.FADE -> {
                // Smooth sine wave fade: 1.5 second cycle
                val wave = ((sin(currentTime / 240.0) + 1.0) / 2.0).toFloat() // 0..1
                currentAlpha = (originalAlpha * (0.15f + 0.85f * wave)).toInt().coerceIn(0, 255)
            }
        }

        textPaint.alpha = currentAlpha
        glowPaint.alpha = currentAlpha

        // 4. Draw Neon Glow & Text
        if (currentAlpha > 0) {
            // Outer glow layer
            canvas.drawText(text, posX, posY, glowPaint)
            // Core text layer
            canvas.drawText(text, posX, posY, textPaint)
        }

        if (isAnimating) {
            postInvalidateOnAnimation()
        }
    }
}
