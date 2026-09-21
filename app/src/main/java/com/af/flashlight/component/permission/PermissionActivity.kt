package com.af.flashlight.component.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.base.resultcontract.SettingPermissionResultContract
import com.af.flashlight.component.main.activity.MainActivity
import com.af.flashlight.databinding.ActivityPermissionBinding
import com.af.flashlight.utils.Permission
import com.af.flashlight.utils.SpManager
import com.af.flashlight.utils.isPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private var hasRequestedCameraPermission: Boolean
        get() = spManager.getBoolean(KEY_REQUESTED_CAMERA_PERMISSION, false)
        set(value) = spManager.putBoolean(KEY_REQUESTED_CAMERA_PERMISSION, value)

    private val settingPermissionLauncher =
        registerForActivityResult(SettingPermissionResultContract()) {
            updateUiState()
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            updateUiState()
        }

    override fun provideViewBinding(): ActivityPermissionBinding =
        ActivityPermissionBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setFullscreen()

        tvDescription.text = getString(
            R.string.permission_description,
            getString(R.string.app_name)
        )

        layoutCameraPermission.setOnClickListener {
            handleCameraPermissionClick()
        }

        btnContinue.setOnClickListener {
            if (isCameraPermissionGranted()) {
                MainActivity.startNewTask(this@PermissionActivity)
                finish()
            }
        }

        updateUiState()
    }

    override fun onResume() {
        super.onResume()
        updateUiState()
    }

    private fun isCameraPermissionGranted(): Boolean =
        isPermissionGranted(Permission.CAMERA)

    private fun updateUiState() = with(viewBinding) {
        val isGranted = isCameraPermissionGranted()
        switchCamera.isChecked = isGranted
        btnContinue.isEnabled = isGranted
    }

    private fun handleCameraPermissionClick() {
        if (isCameraPermissionGranted()) {
            return
        }

        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.CAMERA
        )

        if (hasRequestedCameraPermission && !showRationale) {
            // Người dùng đã từ chối vĩnh viễn (Don't ask again) -> Mở cài đặt chi tiết ứng dụng
            settingPermissionLauncher.launch(0)
        } else {
            hasRequestedCameraPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    companion object {
        private const val KEY_REQUESTED_CAMERA_PERMISSION = "KEY_REQUESTED_CAMERA_PERMISSION"

        fun start(context: Context) {
            Intent(context, PermissionActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
