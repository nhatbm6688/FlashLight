package com.af.flashlight.dialog

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogForceUpdateBinding

class ForceUpdateDialog(private val context: Context) :
    BaseDialog<DialogForceUpdateBinding>(context) {

    var onExit: () -> Unit = {}

    override fun provideViewBinding(): DialogForceUpdateBinding {
        return DialogForceUpdateBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnUpdate.setOnClickListener {
            openPlayStore()
        }

        btnExit.setOnClickListener {
            dismiss()
            onExit.invoke()
        }
    }

    private fun openPlayStore() {
        val packageName = context.packageName
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$packageName".toUri()
                )
            )
        }
    }
}
