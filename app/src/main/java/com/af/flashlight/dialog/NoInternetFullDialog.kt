package com.af.flashlight.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import com.af.flashlight.R
import com.af.flashlight.base.dialog.BaseFullScreenDialogFragment
import com.af.flashlight.databinding.DialogNoInternetFullBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class NoInternetFullDialog : BaseFullScreenDialogFragment<DialogNoInternetFullBinding>(R.layout.dialog_no_internet_full) {


    override fun inflateDialogBinding(inflater: LayoutInflater, container: ViewGroup?): DialogNoInternetFullBinding =
        DialogNoInternetFullBinding.inflate(inflater, container, false)

    override var allowBackToCancel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.setBackgroundDrawable(Color.BLACK.toDrawable())
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        btnGoToSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
        btnCancel.setOnClickListener {
            requireActivity().finishAffinity()
            exitProcess(0)
        }
    }

}
