package com.af.flashlight.component.screenlight

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.af.flashlight.R
import com.af.flashlight.base.fragment.BaseFragment
import com.af.flashlight.component.screenlight.viewmodel.ScreenLightUiState
import com.af.flashlight.component.screenlight.viewmodel.ScreenLightViewModel
import com.af.flashlight.databinding.FragmentScreenLightBinding
import com.af.flashlight.dialog.ColorPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class ScreenLightFragment : BaseFragment<FragmentScreenLightBinding>() {

    private val viewModel: ScreenLightViewModel by viewModels()

    private var colorCyan: Int = 0
    private var colorWhite: Int = 0
    private var colorYellow: Int = 0
    private var colorGreen: Int = 0
    private var colorPurple: Int = 0
    private var colorCoral: Int = 0

    override fun provideViewBinding(container: ViewGroup?): FragmentScreenLightBinding {
        return FragmentScreenLightBinding.inflate(layoutInflater, container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        val context = requireContext()

        // Load preset colors from Design System tokens
        colorCyan = ContextCompat.getColor(context, R.color.color_screenlight_preset_cyan)
        colorWhite = ContextCompat.getColor(context, R.color.color_screenlight_preset_white)
        colorYellow = ContextCompat.getColor(context, R.color.color_screenlight_preset_yellow)
        colorGreen = ContextCompat.getColor(context, R.color.color_screenlight_preset_green)
        colorPurple = ContextCompat.getColor(context, R.color.color_screenlight_preset_purple)
        colorCoral = ContextCompat.getColor(context, R.color.color_screenlight_preset_coral)

        viewModel.initDefaultColorIfEmpty(colorCyan)

        initColorCircles()
        initBrightnessControl()
        initActions()

        resetBrightness()
    }

    override fun initObserver() {
        super.initObserver()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderUi(state)
                }
            }
        }
    }

    private fun renderUi(state: ScreenLightUiState) = with(viewBinding) {
        val displayColor = calculatePreviewColor(state.currentColor, state.currentBrightness)
        viewLightPreview.setBackgroundColor(displayColor)

        ringColorCyan.isVisible = (state.currentColor == colorCyan)
        ringColorWhite.isVisible = (state.currentColor == colorWhite)
        ringColorYellow.isVisible = (state.currentColor == colorYellow)
        ringColorGreen.isVisible = (state.currentColor == colorGreen)
        ringColorPurple.isVisible = (state.currentColor == colorPurple)
        ringColorCoral.isVisible = (state.currentColor == colorCoral)

        val isPreset = (state.currentColor == colorCyan || state.currentColor == colorWhite ||
                state.currentColor == colorYellow || state.currentColor == colorGreen ||
                state.currentColor == colorPurple || state.currentColor == colorCoral)

        ringColorPicker.isVisible = !isPreset

        if (sbBrightness.progress != state.currentBrightness) {
            sbBrightness.progress = state.currentBrightness
        }
        tvBrightnessPercent.text = "${state.currentBrightness}%"

        resetBrightness()
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

        containerColorCyan.setOnClickListener { viewModel.selectColor(colorCyan) }
        containerColorWhite.setOnClickListener { viewModel.selectColor(colorWhite) }
        containerColorYellow.setOnClickListener { viewModel.selectColor(colorYellow) }
        containerColorGreen.setOnClickListener { viewModel.selectColor(colorGreen) }
        containerColorPurple.setOnClickListener { viewModel.selectColor(colorPurple) }
        containerColorCoral.setOnClickListener { viewModel.selectColor(colorCoral) }

        btnCustomColorPicker.setOnClickListener {
            val current = viewModel.uiState.value.currentColor
            ColorPickerDialog(requireContext(), current) { selectedColor ->
                viewModel.selectColor(selectedColor)
            }.show()
        }
    }

    private fun initBrightnessControl() = with(viewBinding) {
        val current = viewModel.uiState.value.currentBrightness
        sbBrightness.progress = current
        tvBrightnessPercent.text = "$current%"

        sbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val clamped = progress.coerceAtLeast(5)
                tvBrightnessPercent.text = "$clamped%"
                if (fromUser) {
                    val previewColor = calculatePreviewColor(viewModel.uiState.value.currentColor, clamped)
                    viewLightPreview.setBackgroundColor(previewColor)
                    viewModel.previewBrightness(clamped)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val clamped = seekBar?.progress?.coerceAtLeast(5) ?: 80
                viewModel.updateBrightness(clamped)
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

    private fun calculatePreviewColor(color: Int, brightness: Int): Int {
        val factor = brightness.coerceIn(5, 100) / 100f
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).roundToInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).roundToInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).roundToInt().coerceIn(0, 255)
        return Color.argb(a, r, g, b)
    }

    fun restoreBrightness() {
        resetBrightness()
    }

    fun resetBrightness() {
        activity?.let { act ->
            val lp = act.window.attributes
            if (lp.screenBrightness != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                act.window.attributes = lp
            }
        }
    }

    private fun openPlayMode() {
        val state = viewModel.uiState.value
        ScreenLightPlayActivity.start(requireContext(), state.currentColor, state.currentBrightness)
    }

    override fun onResume() {
        super.onResume()
        resetBrightness()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        resetBrightness()
    }

    override fun onPause() {
        super.onPause()
        resetBrightness()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetBrightness()
    }

    companion object {
        fun newInstance(): ScreenLightFragment = ScreenLightFragment()
    }
}
