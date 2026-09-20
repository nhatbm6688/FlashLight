@file:Suppress("DEPRECATION")

package com.af.flashlight.base.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.af.flashlight.R

abstract class BaseFullScreenDialogFragment<V : ViewBinding>(
    @LayoutRes contentLayoutId: Int,
) : DialogFragment(contentLayoutId) {
    private var _viewBinding: V? = null

    val viewBinding
        get() =
            _viewBinding
                ?: throw kotlin.IllegalStateException("Binding cannot be accessed before or after the activity is destroyed.")

    abstract fun inflateDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): V

    open var allowBackToCancel: Boolean = false
    open val useBottomSheetAnimation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _viewBinding = inflateDialogBinding(inflater, container)
        return viewBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initAction()
        initData()
        initObserver()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = allowBackToCancel
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        if (useBottomSheetAnimation) window.setWindowAnimations(R.style.BottomSheetAnimation)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onDetach() {
        super.onDetach()
        // Không gọi hideNavigationBar() ở đây — onWindowFocusChanged của Activity sẽ xử lý
    }

    open fun initViews() {}

    open fun initData() {}

    open fun initAction() {}

    open fun initObserver() {}

    override fun dismiss() {
        super.dismiss()
        activity?.window?.decorView?.post {
            activity?.window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }
}
