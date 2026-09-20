package com.af.flashlight.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.af.flashlight.R

abstract class BaseDialog<V : ViewBinding>(context: Context, theme: Int = R.style.Theme_Dialog) :
    Dialog(context, theme) {
    lateinit var viewBinding: V

    init {
        window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = provideViewBinding()
        setContentView(viewBinding.root)
        initViews()
        initData()
    }

    abstract fun provideViewBinding(): V
    open fun initViews() {}
    open fun initData() {}
}
