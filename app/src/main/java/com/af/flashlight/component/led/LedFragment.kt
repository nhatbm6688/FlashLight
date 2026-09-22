package com.af.flashlight.component.led

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.af.flashlight.R
import com.af.flashlight.base.fragment.BaseFragment
import com.af.flashlight.component.led.adapter.LedBackgroundAdapter
import com.af.flashlight.component.led.model.LedBackgroundItem
import com.af.flashlight.component.led.model.LedDirection
import com.af.flashlight.component.led.model.LedEffect
import com.af.flashlight.component.led.viewmodel.LedViewModel
import com.af.flashlight.databinding.FragmentLedBinding
import com.af.flashlight.dialog.ColorPickerDialog
import kotlinx.coroutines.launch

class LedFragment : BaseFragment<FragmentLedBinding>() {

    // ──────────────────────────────────────────────────────────────────────
    // ViewModel — survives navigate-away and back
    // ──────────────────────────────────────────────────────────────────────
    private val viewModel: LedViewModel by viewModels()

    // Preset colors (resolved once, require Context)
    private var colorCyan: Int = 0
    private var colorWhite: Int = 0
    private var colorYellow: Int = 0
    private var colorGreen: Int = 0
    private var colorPurple: Int = 0
    private var colorCoral: Int = 0

    private lateinit var backgroundAdapter: LedBackgroundAdapter

    // Suppress TextWatcher re-entrancy when restoring text from ViewModel
    private var suppressTextWatcher = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        val result = viewModel.addCustomBackground(uri)
        backgroundAdapter.applyAddResult(result.insertedPosition, result.removedPosition)
        applyBackground(bgRes = 0, bgUri = uri)
    }

    // ──────────────────────────────────────────────────────────────────────
    override fun provideViewBinding(container: ViewGroup?): FragmentLedBinding {
        return FragmentLedBinding.inflate(layoutInflater, container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        val context = requireContext()

        // Resolve colors once
        colorCyan   = ContextCompat.getColor(context, R.color.color_screenlight_preset_cyan)
        colorWhite  = ContextCompat.getColor(context, R.color.color_screenlight_preset_white)
        colorYellow = ContextCompat.getColor(context, R.color.color_screenlight_preset_yellow)
        colorGreen  = ContextCompat.getColor(context, R.color.color_screenlight_preset_green)
        colorPurple = ContextCompat.getColor(context, R.color.color_screenlight_preset_purple)
        colorCoral  = ContextCompat.getColor(context, R.color.color_screenlight_preset_coral)

        // Ensure ViewModel has a default color (yellow) on very first run
        if (viewModel.state.value.color == 0) {
            viewModel.updateColor(colorYellow)
        }

        setupColorCircles()
        setupTextWatcher()
        setupSliders()
        setupDirections()
        setupVisualEffects()
        setupBackgrounds()

        btnExpand.setOnClickListener { openPlayMode() }
        cardLedPreview.setOnClickListener { openPlayMode() }
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Restore full UI from ViewModel each time the view is recreated
        observeState()
    }

    // ──────────────────────────────────────────────────────────────────────
    // Observe & Restore state
    // ──────────────────────────────────────────────────────────────────────
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { s ->
                    restoreUi(s)
                }
            }
        }
    }

    private fun restoreUi(s: com.af.flashlight.component.led.viewmodel.LedEditorState) =
        with(viewBinding) {
            // --- Text ---
            val textToShow = s.text
            if (etLedText.text?.toString() != textToShow) {
                suppressTextWatcher = true
                etLedText.setText(textToShow)
                suppressTextWatcher = false
            }
            viewLedPreview.text = textToShow

            // --- Color ---
            val color = if (s.color == 0) colorYellow else s.color
            viewLedPreview.ledColor = color
            restoreColorRingUi(color)

            // --- Font size ---
            val fontProgress = ((s.fontSize - 24f) / 0.8f).toInt().coerceIn(0, 100)
            if (sbFontSize.progress != fontProgress) sbFontSize.progress = fontProgress
            tvFontSizeValue.text = "${s.fontSize.toInt()}px"
            viewLedPreview.fontSizeSp = s.fontSize

            // --- Speed ---
            val speedProgress = s.speed - 1
            if (sbScrollSpeed.progress != speedProgress) sbScrollSpeed.progress = speedProgress
            tvScrollSpeedValue.text = "${s.speed}s"
            viewLedPreview.speedLevel = s.speed

            // --- Direction ---
            updateDirectionButtons(s.direction)

            // --- Effect ---
            updateEffectButtons(s.effect)

            // --- Background ---
            applyBackground(s.bgRes, s.bgUri)

            // --- Background adapter items ---
            if (::backgroundAdapter.isInitialized) {
                backgroundAdapter.restoreItems(s.backgroundItems, s.selectedBgPosition)
            }
        }

    // ──────────────────────────────────────────────────────────────────────
    // Setup helpers (called once in initViews)
    // ──────────────────────────────────────────────────────────────────────
    private fun setupColorCircles() = with(viewBinding) {
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

        containerColorCyan.setOnClickListener   { selectColor(colorCyan,   ringColorCyan) }
        containerColorWhite.setOnClickListener  { selectColor(colorWhite,  ringColorWhite) }
        containerColorYellow.setOnClickListener { selectColor(colorYellow, ringColorYellow) }
        containerColorGreen.setOnClickListener  { selectColor(colorGreen,  ringColorGreen) }
        containerColorPurple.setOnClickListener { selectColor(colorPurple, ringColorPurple) }
        containerColorCoral.setOnClickListener  { selectColor(colorCoral,  ringColorCoral) }

        btnCustomColorPicker.setOnClickListener {
            val current = if (viewModel.state.value.color == 0) colorYellow else viewModel.state.value.color
            ColorPickerDialog(requireContext(), current) { selectedColor ->
                selectColor(selectedColor, ringColorPicker)
            }.show()
        }
    }

    private fun selectColor(color: Int, activeRing: android.view.View) = with(viewBinding) {
        viewModel.updateColor(color)
        viewLedPreview.ledColor = color
        restoreColorRingUi(color)
        if (activeRing == ringColorPicker) {
            btnCustomColorPicker.imageTintList = ColorStateList.valueOf(color)
        }
    }

    /** Decide which selection ring to show based on saved color value. */
    private fun restoreColorRingUi(color: Int) = with(viewBinding) {
        ringColorCyan.isVisible   = (color == colorCyan)
        ringColorWhite.isVisible  = (color == colorWhite)
        ringColorYellow.isVisible = (color == colorYellow)
        ringColorGreen.isVisible  = (color == colorGreen)
        ringColorPurple.isVisible = (color == colorPurple)
        ringColorCoral.isVisible  = (color == colorCoral)
        val isCustom = color != colorCyan && color != colorWhite && color != colorYellow &&
                color != colorGreen && color != colorPurple && color != colorCoral
        ringColorPicker.isVisible = isCustom
        if (!isCustom) {
            btnCustomColorPicker.imageTintList = null
        }
    }

    private fun setupTextWatcher() = with(viewBinding) {
        etLedText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (suppressTextWatcher) return
                val input = s?.toString()?.trim()
                val text = if (input.isNullOrBlank()) getString(R.string.default_led_text) else s.toString()
                viewModel.updateText(text)
                viewLedPreview.text = text
            }
        })
    }

    private fun setupSliders() = with(viewBinding) {
        sbFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val size = 24f + progress * 0.8f
                viewModel.updateFontSize(size)
                tvFontSizeValue.text = "${size.toInt()}px"
                viewLedPreview.fontSizeSp = size
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbScrollSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val speed = progress + 1
                viewModel.updateSpeed(speed)
                tvScrollSpeedValue.text = "${speed}s"
                viewLedPreview.speedLevel = speed
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupDirections() = with(viewBinding) {
        btnDirectionRight.setOnClickListener { selectDirection(LedDirection.RIGHT) }
        btnDirectionLeft.setOnClickListener  { selectDirection(LedDirection.LEFT)  }
        btnDirectionDown.setOnClickListener  { selectDirection(LedDirection.DOWN)  }
        btnDirectionUp.setOnClickListener    { selectDirection(LedDirection.UP)    }
    }

    private fun selectDirection(direction: LedDirection) {
        viewModel.updateDirection(direction)
        updateDirectionButtons(direction)
    }

    private fun updateDirectionButtons(direction: LedDirection) = with(viewBinding) {
        btnDirectionRight.isSelected = (direction == LedDirection.RIGHT)
        btnDirectionLeft.isSelected  = (direction == LedDirection.LEFT)
        btnDirectionDown.isSelected  = (direction == LedDirection.DOWN)
        btnDirectionUp.isSelected    = (direction == LedDirection.UP)
        viewLedPreview.direction = direction
    }

    private fun setupVisualEffects() = with(viewBinding) {
        btnEffectGlow.setOnClickListener  { selectEffect(LedEffect.GLOW)  }
        btnEffectBlink.setOnClickListener { selectEffect(LedEffect.BLINK) }
        btnEffectNeon.setOnClickListener  { selectEffect(LedEffect.NEON)  }
        btnEffectFade.setOnClickListener  { selectEffect(LedEffect.FADE)  }
    }

    private fun selectEffect(effect: LedEffect) {
        viewModel.updateEffect(effect)
        updateEffectButtons(effect)
    }

    private fun updateEffectButtons(effect: LedEffect) = with(viewBinding) {
        btnEffectGlow.isSelected  = (effect == LedEffect.GLOW)
        btnEffectBlink.isSelected = (effect == LedEffect.BLINK)
        btnEffectNeon.isSelected  = (effect == LedEffect.NEON)
        btnEffectFade.isSelected  = (effect == LedEffect.FADE)
        viewLedPreview.effect = effect
    }

    private fun setupBackgrounds() = with(viewBinding) {
        // Build preset list (add button + preset images)
        val presets = mutableListOf<LedBackgroundItem>()
        presets.add(LedBackgroundItem(id = 0, isAddButton = true))
        val presetRes = listOf(
            R.mipmap.bg_led_1, R.mipmap.bg_led_2, R.mipmap.bg_led_3,
            R.mipmap.bg_led_4, R.mipmap.bg_led_5, R.mipmap.bg_led_6,
            R.mipmap.bg_led_7, R.mipmap.bg_led_8, R.mipmap.bg_led_9,
            R.mipmap.bg_led_10
        )
        presetRes.forEachIndexed { index, resId ->
            presets.add(LedBackgroundItem(id = index + 1, resId = resId, isSelected = (index == 0)))
        }

        // ViewModel initialises the list only once (no-op on subsequent navigations)
        viewModel.initBackgroundsIfEmpty(presets, R.mipmap.bg_led_1)

        val vmState = viewModel.state.value

        backgroundAdapter = LedBackgroundAdapter(
            items = vmState.backgroundItems.ifEmpty { presets },
            onAddClicked = { pickImageLauncher.launch("image/*") },
            onBackgroundSelected = { item, position ->
                viewModel.selectBackground(position)
                val s = viewModel.state.value
                applyBackground(s.bgRes, s.bgUri)
            }
        )
        backgroundAdapter.restoreItems(
            vmState.backgroundItems.ifEmpty { presets },
            vmState.selectedBgPosition
        )

        rvBackgrounds.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        rvBackgrounds.adapter = backgroundAdapter
    }

    // ──────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────
    private fun applyBackground(bgRes: Int, bgUri: Uri?) {
        if (bgUri != null) {
            viewBinding.viewLedPreview.setBackgroundUri(bgUri)
        } else if (bgRes != 0) {
            viewBinding.viewLedPreview.setBackgroundResource(bgRes)
        }
    }

    private fun openPlayMode() {
        val s = viewModel.state.value
        LedPlayActivity.start(
            context = requireContext(),
            text = s.text,
            color = if (s.color == 0) colorYellow else s.color,
            fontSize = s.fontSize,
            speed = s.speed,
            direction = s.direction,
            effect = s.effect,
            bgRes = s.bgRes,
            bgUri = s.bgUri
        )
    }

    companion object {
        fun newInstance(): LedFragment = LedFragment()
    }
}
