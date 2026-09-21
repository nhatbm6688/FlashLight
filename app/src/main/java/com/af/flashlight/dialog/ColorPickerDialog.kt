package com.af.flashlight.dialog

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogColorPickerBinding

class ColorPickerDialog(
    context: Context,
    private val initialColor: Int,
    private val onColorSelected: (Int) -> Unit
) : BaseDialog<DialogColorPickerBinding>(context) {

    private var alphaVal: Int = Color.alpha(initialColor)
    private var hue: Float = 0f
    private var saturation: Float = 0f
    private var value: Float = 1f
    private var isUpdatingFromInput = false

    override fun provideViewBinding(): DialogColorPickerBinding {
        return DialogColorPickerBinding.inflate(LayoutInflater.from(context))
    }

    override fun initViews() {
        super.initViews()

        val hsv = FloatArray(3)
        Color.colorToHSV(initialColor, hsv)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        if (alphaVal == 0) alphaVal = 255

        with(viewBinding) {
            satValPanel.hue = hue
            satValPanel.setSatVal(saturation, value)
            hueSlider.hue = hue

            val solidRgb = Color.HSVToColor(floatArrayOf(hue, saturation, value))
            alphaSlider.argbColor = solidRgb
            alphaSlider.alphaVal = alphaVal

            updateOutputs()

            satValPanel.onSatValChanged = { sat, v ->
                saturation = sat
                value = v
                val currentSolid = Color.HSVToColor(floatArrayOf(hue, saturation, value))
                alphaSlider.argbColor = currentSolid
                updateOutputs()
            }

            hueSlider.onHueChanged = { h ->
                hue = h
                satValPanel.hue = h
                val currentSolid = Color.HSVToColor(floatArrayOf(hue, saturation, value))
                alphaSlider.argbColor = currentSolid
                updateOutputs()
            }

            alphaSlider.onAlphaChanged = { a ->
                alphaVal = a
                updateOutputs()
            }

            etHex.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isUpdatingFromInput) return
                    val hex = s?.toString()?.trim() ?: return
                    if (hex.startsWith("#") && (hex.length == 7 || hex.length == 9)) {
                        try {
                            val parsed = Color.parseColor(hex)
                            val parsedHsv = FloatArray(3)
                            Color.colorToHSV(parsed, parsedHsv)
                            hue = parsedHsv[0]
                            saturation = parsedHsv[1]
                            value = parsedHsv[2]
                            alphaVal = Color.alpha(parsed)

                            satValPanel.hue = hue
                            satValPanel.setSatVal(saturation, value)
                            hueSlider.hue = hue
                            val currentSolid = Color.HSVToColor(floatArrayOf(hue, saturation, value))
                            alphaSlider.argbColor = currentSolid
                            alphaSlider.alphaVal = alphaVal

                            tvR.text = Color.red(parsed).toString()
                            tvG.text = Color.green(parsed).toString()
                            tvB.text = Color.blue(parsed).toString()
                        } catch (_: Exception) {}
                    }
                }
            })

            btnChoose.setOnClickListener {
                val finalColor = getCurrentColor()
                onColorSelected(finalColor)
                dismiss()
            }
        }
    }

    private fun getCurrentColor(): Int {
        val hsv = floatArrayOf(hue, saturation, value)
        val rgb = Color.HSVToColor(hsv)
        return Color.argb(alphaVal, Color.red(rgb), Color.green(rgb), Color.blue(rgb))
    }

    private fun updateOutputs() = with(viewBinding) {
        val color = getCurrentColor()
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        tvR.text = r.toString()
        tvG.text = g.toString()
        tvB.text = b.toString()

        isUpdatingFromInput = true
        etHex.setText(String.format("#%06X", (0xFFFFFF and color)))
        isUpdatingFromInput = false
    }
}
