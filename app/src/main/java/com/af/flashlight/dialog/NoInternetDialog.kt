package com.af.flashlight.dialog

import android.content.Context
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogNoInternetBinding

class NoInternetDialog(private val context: Context) :
    BaseDialog<DialogNoInternetBinding>(context) {

    var onRetry: () -> Unit = {}
    var onCancel: () -> Unit = {}

    override fun provideViewBinding(): DialogNoInternetBinding {
        return DialogNoInternetBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnRetry.setOnClickListener {
            dismiss()
            onRetry.invoke()
        }

        btnCancel.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
    }

}
