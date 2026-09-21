package com.af.flashlight.dialog

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogNoInternetBinding

class NoInternetDialog(private val context: Context) :
    BaseDialog<DialogNoInternetBinding>(context) {

    var onGoToSetting: () -> Unit = {
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
    var onCancel: () -> Unit = {}

    override fun provideViewBinding(): DialogNoInternetBinding {
        return DialogNoInternetBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnGoToSetting.setOnClickListener {
            dismiss()
            onGoToSetting.invoke()
        }

        btnCancel.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
    }

}
