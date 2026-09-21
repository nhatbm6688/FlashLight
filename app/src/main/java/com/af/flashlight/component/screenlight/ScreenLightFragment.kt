package com.af.flashlight.component.screenlight

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.af.flashlight.R
import com.af.flashlight.base.fragment.BaseFragment
import com.af.flashlight.databinding.FragmentScreenLightBinding
import com.af.flashlight.dialog.ColorPickerDialog
import androidx.core.content.ContextCompat
import com.af.flashlight.utils.SpManager

class ScreenLightFragment : BaseFragment<FragmentScreenLightBinding>() {

    private lateinit var spManager: SpManager

    private var colorCyan: Int = 0
    private var colorWhite: Int = 0
    private var colorYellow: Int = 0
    private var colorGreen: Int = 0
    private var colorPurple: Int = 0
    private var colorCoral: Int = 0

    private var currentColor: Int = 0
    private var currentBrightness: Int = 80

    override fun provideViewBinding(container: ViewGroup?): FragmentScreenLightBinding {
        return FragmentScreenLightBinding.inflate(layoutInflater, container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        val context = requireContext()
        spManager = SpManager.getInstance(context)

        // Load colors from Design System tokens
        colorCyan = ContextCompat.getColor(context, R.color.color_screenlight_preset_cyan)
        colorWhite = ContextCompat.getColor(context, R.color.color_screenlight_preset_white)
        colorYellow = ContextCompat.getColor(context, R.color.color_screenlight_preset_yellow)
        colorGreen = ContextCompat.getColor(context, R.color.color_screenlight_preset_green)
        colorPurple = ContextCompat.getColor(context, R.color.color_screenlight_preset_purple)
        colorCoral = ContextCompat.getColor(context, R.color.color_screenlight_preset_coral)

        currentColor = spManager.getScreenLightColor()
        if (currentColor == 0) {
            currentColor = colorCyan
        }
        currentBrightness = spManager.getScreenLightBrightness()

        initColorCircles()
        initBrightnessControl()
        initActions()

        applyCurrentState()
    }

    private fun initColorCircles() = with(viewBinding) {
        circleColorCyan.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorCyan.backgroundTintList = ColorStateList.valueOf(colorCyan)

        circleColorWhite.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorWhite.backgroundTintList = ColorStateList.valueOf(colorWhite)

        circleColorYellow.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorYellow.backgroundTintList = ColorStateList.valueOf(colorYellow)

        circleColorGreen.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorGreen.backgroundTintList = ColorStateList.valueOf(colorGreen)

        circleColorPurple.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorPurple.backgroundTintList = ColorStateList.valueOf(colorPurple)

        circleColorCoral.setBackgroundResource(R.drawable.bg_circle_color)
        circleColorCoral.backgroundTintList = ColorStateList.valueOf(colorCoral)

        containerColorCyan.setOnClickListener { selectColor(colorCyan) }
        containerColorWhite.setOnClickListener { selectColor(colorWhite) }
        containerColorYellow.setOnClickListener { selectColor(colorYellow) }
        containerColorGreen.setOnClickListener { selectColor(colorGreen) }
        containerColorPurple.setOnClickListener { selectColor(colorPurple) }
        containerColorCoral.setOnClickListener { selectColor(colorCoral) }

        btnCustomColorPicker.setOnClickListener {
            ColorPickerDialog(requireContext(), currentColor) { selectedColor ->
                selectColor(selectedColor)
            }.show()
        }
    }

    private fun initBrightnessControl() = with(viewBinding) {
        sbBrightness.progress = currentBrightness
        tvBrightnessPercent.text = "$currentBrightness%"

        sbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val clamped = progress.coerceAtLeast(5)
                currentBrightness = clamped
                tvBrightnessPercent.text = "$clamped%"
                if (fromUser) {
                    applyScreenBrightness(clamped)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                spManager.setScreenLightBrightness(currentBrightness)
            }
        })
    }

    private fun initActions() = with(viewBinding) {
        btnExpand.setOnClickListener {
            openPlayMode()
        }
        cardLightPreview.setOnClickListener {
            openPlayMode()
        }
    }

    private fun selectColor(color: Int) {
        currentColor = color
        spManager.setScreenLightColor(color)
        applyCurrentState()
    }

    private fun applyCurrentState() = with(viewBinding) {
        viewLightPreview.setBackgroundColor(currentColor)

        ringColorCyan.isVisible = (currentColor == colorCyan)
        ringColorWhite.isVisible = (currentColor == colorWhite)
        ringColorYellow.isVisible = (currentColor == colorYellow)
        ringColorGreen.isVisible = (currentColor == colorGreen)
        ringColorPurple.isVisible = (currentColor == colorPurple)
        ringColorCoral.isVisible = (currentColor == colorCoral)

        val isPreset = (currentColor == colorCyan || currentColor == colorWhite ||
                currentColor == colorYellow || currentColor == colorGreen ||
                currentColor == colorPurple || currentColor == colorCoral)

        ringColorPicker.isVisible = !isPreset

        applyScreenBrightness(currentBrightness)
    }

    private fun applyScreenBrightness(brightness: Int) {
        activity?.let { act ->
            val lp = act.window.attributes
            lp.screenBrightness = (brightness.coerceIn(5, 100)) / 100f
            act.window.attributes = lp
        }
    }

    private fun openPlayMode() {
        ScreenLightPlayActivity.start(requireContext(), currentColor, currentBrightness)
    }

    override fun onResume() {
        super.onResume()
        applyScreenBrightness(currentBrightness)
    }

    companion object {
        fun newInstance(): ScreenLightFragment = ScreenLightFragment()
    }
}
