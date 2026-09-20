package com.af.flashlight.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.af.flashlight.R
import com.af.flashlight.utils.Utils

class LoadingDialog : Dialog {
    private var mContext: Context

    private var dialog: LoadingDialog? = null

    constructor(context: Context) : super(context) {
        mContext = context
        dialog = LoadingDialog(mContext, R.style.CustomProgressLoading)
        dialog?.setTitle("")
        dialog?.setContentView(R.layout.dialog_loading)
        dialog?.setCancelable(false)
        dialog?.window?.attributes?.gravity = Gravity.CENTER
        val lp = dialog?.window?.attributes
        lp?.dimAmount = 0.2f
        dialog?.window?.attributes = lp
    }

    override fun show() {
        this.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        super.show()
        window?.let { Utils.fullScreenImmersive(it) }
        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        mContext = context
    }

    fun showProgressDialog() {
        if (dialog != null && !dialog!!.isShowing) {
            dialog?.show()
        }
    }

    fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }

}
