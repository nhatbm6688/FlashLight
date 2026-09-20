package com.af.flashlight.dialog

import android.content.Context
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogExitAppBinding

class ExitAppDialog(private val context: Context) :
    BaseDialog<DialogExitAppBinding>(context) {

    var onExit: () -> Unit = {}

    override fun provideViewBinding(): DialogExitAppBinding {
        return DialogExitAppBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnKeepUsingApp.setOnClickListener {
            dismiss()
        }

        btnExitApp.setOnClickListener {
            dismiss()
            onExit.invoke()
        }
    }
}

