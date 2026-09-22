package com.af.flashlight.component.flashalert.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.flashalert.model.FlashAlertType
import com.af.flashlight.component.flashalert.viewmodel.FlashAlertViewModel
import com.af.flashlight.databinding.ActivityFlashAlertConfigBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class FlashAlertConfigActivity : BaseActivity<ActivityFlashAlertConfigBinding>() {

    private val viewModel: FlashAlertViewModel by viewModels()
    private var alertType: FlashAlertType = FlashAlertType.CALL

    private val requestPhonePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setCallEnabled(true)
            } else {
                viewBinding.switchStatus.isChecked = false
                showToast(getString(R.string.permission_phone_needed))
            }
        }

    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setSmsEnabled(true)
            } else {
                viewBinding.switchStatus.isChecked = false
                showToast(getString(R.string.permission_sms_needed))
            }
        }

    override fun provideViewBinding(): ActivityFlashAlertConfigBinding =
        ActivityFlashAlertConfigBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        val typeName = intent.getStringExtra(EXTRA_ALERT_TYPE)
        alertType = try {
            FlashAlertType.valueOf(typeName ?: FlashAlertType.CALL.name)
        } catch (_: Exception) {
            FlashAlertType.CALL
        }

        toolBar.btnBack.setOnClickListener { finish() }

        when (alertType) {
            FlashAlertType.CALL -> {
                toolBar.tvTitle.text = getString(R.string.incoming_calls)
                ivHeroIcon.setImageResource(R.drawable.ic_call_big)
                cardSelectApp.visibility = View.GONE
            }

            FlashAlertType.SMS -> {
                toolBar.tvTitle.text = getString(R.string.sms)
                ivHeroIcon.setImageResource(R.drawable.ic_sms_big)
                cardSelectApp.visibility = View.GONE
            }

            FlashAlertType.NOTIFICATION -> {
                toolBar.tvTitle.text = getString(R.string.notification)
                ivHeroIcon.setImageResource(R.drawable.ic_notification_big)
                cardSelectApp.visibility = View.VISIBLE
                cardSelectApp.setOnClickListener {
                    FlashAlertSelectAppActivity.start(this@FlashAlertConfigActivity)
                }
            }
        }

        switchStatus.setOnCheckedChangeListener { _, isChecked ->
            tvStatusText.text = getString(
                if (isChecked) R.string.status_colon_on else R.string.status_colon_off
            )

            if (isChecked) {
                when (alertType) {
                    FlashAlertType.CALL -> {
                        if (ContextCompat.checkSelfPermission(
                                this@FlashAlertConfigActivity,
                                Manifest.permission.READ_PHONE_STATE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPhonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                        } else {
                            viewModel.setCallEnabled(true)
                        }
                    }

                    FlashAlertType.SMS -> {
                        if (ContextCompat.checkSelfPermission(
                                this@FlashAlertConfigActivity,
                                Manifest.permission.RECEIVE_SMS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestSmsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
                        } else {
                            viewModel.setSmsEnabled(true)
                        }
                    }

                    FlashAlertType.NOTIFICATION -> {
                        val isNotificationListenerGranted = NotificationManagerCompat
                            .getEnabledListenerPackages(this@FlashAlertConfigActivity)
                            .contains(packageName)

                        if (!isNotificationListenerGranted) {
                            showToast(getString(R.string.notification_access_needed))
                            try {
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            } catch (_: Exception) {}
                        }
                        viewModel.setNotiEnabled(true)
                    }
                }
            } else {
                when (alertType) {
                    FlashAlertType.CALL -> viewModel.setCallEnabled(false)
                    FlashAlertType.SMS -> viewModel.setSmsEnabled(false)
                    FlashAlertType.NOTIFICATION -> viewModel.setNotiEnabled(false)
                }
            }
        }

        sbFlashingOn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val clamped = progress.coerceAtLeast(1)
                val sec = clamped / 10f
                tvFlashingOnVal.text = String.format(Locale.US, "%.1fs", sec)
                if (fromUser) {
                    val ms = clamped * 100L
                    when (alertType) {
                        FlashAlertType.CALL -> viewModel.setCallOnMs(ms)
                        FlashAlertType.SMS -> viewModel.setSmsOnMs(ms)
                        FlashAlertType.NOTIFICATION -> viewModel.setNotiOnMs(ms)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbFlashingOff.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val clamped = progress.coerceAtLeast(1)
                val sec = clamped / 10f
                tvFlashingOffVal.text = String.format(Locale.US, "%.1fs", sec)
                if (fromUser) {
                    val ms = clamped * 100L
                    when (alertType) {
                        FlashAlertType.CALL -> viewModel.setCallOffMs(ms)
                        FlashAlertType.SMS -> viewModel.setSmsOffMs(ms)
                        FlashAlertType.NOTIFICATION -> viewModel.setNotiOffMs(ms)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnTest.setOnClickListener {
            FlashAlertSimulationActivity.start(this@FlashAlertConfigActivity, alertType)
        }
    }

    override fun initObserver() {
        super.initObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    val (enabled, onMs, offMs) = when (alertType) {
                        FlashAlertType.CALL -> Triple(state.isCallEnabled, state.callOnMs, state.callOffMs)
                        FlashAlertType.SMS -> Triple(state.isSmsEnabled, state.smsOnMs, state.smsOffMs)
                        FlashAlertType.NOTIFICATION -> Triple(state.isNotiEnabled, state.notiOnMs, state.notiOffMs)
                    }

                    if (viewBinding.switchStatus.isChecked != enabled) {
                        viewBinding.switchStatus.isChecked = enabled
                    }
                    viewBinding.tvStatusText.text = getString(
                        if (enabled) R.string.status_colon_on else R.string.status_colon_off
                    )

                    val onProgress = (onMs / 100).toInt().coerceIn(1, 20)
                    if (viewBinding.sbFlashingOn.progress != onProgress) {
                        viewBinding.sbFlashingOn.progress = onProgress
                    }
                    viewBinding.tvFlashingOnVal.text = String.format(Locale.US, "%.1fs", onProgress / 10f)

                    val offProgress = (offMs / 100).toInt().coerceIn(1, 20)
                    if (viewBinding.sbFlashingOff.progress != offProgress) {
                        viewBinding.sbFlashingOff.progress = offProgress
                    }
                    viewBinding.tvFlashingOffVal.text = String.format(Locale.US, "%.1fs", offProgress / 10f)

                    if (alertType == FlashAlertType.NOTIFICATION) {
                        val totalCount = maxOf(state.selectedAppPackages.size, state.selectedAppIcons.size)
                        renderAppPreviews(state.selectedAppIcons, totalCount)
                    }
                }
            }
        }
    }

    private fun renderAppPreviews(icons: List<android.graphics.drawable.Drawable>, totalCount: Int) = with(viewBinding) {
        layoutAppPreviews.removeAllViews()
        if (totalCount <= 0 || icons.isEmpty()) {
            return@with
        }

        val inflater = android.view.LayoutInflater.from(this@FlashAlertConfigActivity)

        if (totalCount < 3) {
            val displayCount = minOf(totalCount, icons.size)
            for (i in 0 until displayCount) {
                val itemBinding = com.af.flashlight.databinding.ItemFlashAlertAppPreviewBinding.inflate(inflater, layoutAppPreviews, false)
                itemBinding.ivPreviewIcon.setImageDrawable(icons[i])
                itemBinding.viewDimOverlay.visibility = View.GONE
                itemBinding.tvExtraCount.visibility = View.GONE

                if (i == displayCount - 1) {
                    (itemBinding.root.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.marginEnd = 0
                }
                layoutAppPreviews.addView(itemBinding.root)
            }
        } else {
            // Khi chọn từ 3 app trở lên:
            // 1. Hiển thị 2 app đầu tiên bình thường
            val normalCount = minOf(2, icons.size)
            for (i in 0 until normalCount) {
                val itemBinding = com.af.flashlight.databinding.ItemFlashAlertAppPreviewBinding.inflate(inflater, layoutAppPreviews, false)
                itemBinding.ivPreviewIcon.setImageDrawable(icons[i])
                itemBinding.viewDimOverlay.visibility = View.GONE
                itemBinding.tvExtraCount.visibility = View.GONE
                layoutAppPreviews.addView(itemBinding.root)
            }

            // 2. Ô thứ 3: Hiển thị icon app thứ 3 kèm overlay tối và số lượng app còn lại (+N)
            val badgeBinding = com.af.flashlight.databinding.ItemFlashAlertAppPreviewBinding.inflate(inflater, layoutAppPreviews, false)
            val thirdIcon = icons.getOrNull(2) ?: icons.lastOrNull()
            badgeBinding.ivPreviewIcon.setImageDrawable(thirdIcon)
            badgeBinding.viewDimOverlay.visibility = View.VISIBLE
            badgeBinding.tvExtraCount.visibility = View.VISIBLE

            val extraCount = totalCount - 2
            badgeBinding.tvExtraCount.text = "+$extraCount"

            (badgeBinding.root.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.marginEnd = 0
            layoutAppPreviews.addView(badgeBinding.root)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings()
    }

    companion object {
        private const val EXTRA_ALERT_TYPE = "EXTRA_ALERT_TYPE"

        fun start(activity: Activity, type: FlashAlertType) {
            val intent = Intent(activity, FlashAlertConfigActivity::class.java).apply {
                putExtra(EXTRA_ALERT_TYPE, type.name)
            }
            activity.startActivity(intent)
        }
    }
}
