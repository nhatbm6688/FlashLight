package com.af.flashlight.base.activity

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding
import com.af.network.connectivity.NetworkConnectivityUtils
import com.af.flashlight.data.model.Language
import com.af.flashlight.dialog.LoadingDialog
import com.af.flashlight.dialog.NoInternetFullDialog
import com.af.flashlight.utils.LocaleHelper
import com.af.flashlight.utils.SpManager

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {
    lateinit var viewBinding: V

    lateinit var language: Language

    private lateinit var loadingDialog: LoadingDialog
    private var noInternetDialog: NoInternetFullDialog? = null

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onDestroy() {
        super.onDestroy()
        networkCallback?.let { NetworkConnectivityUtils.unregisterNetworkCallback(this, it) }
    }

    private fun registerNetworkMonitor() {
        networkCallback = NetworkConnectivityUtils.registerNetworkCallback(
            context = this,
            onAvailable = { runOnUiThread { dismissNoInternetDialog() } },
            onLost = { runOnUiThread { showNoInternetDialog() } }
        )
        if (!isNetworkConnected()) showNoInternetDialog()
    }

    private fun isNetworkConnected(): Boolean = NetworkConnectivityUtils.isConnected(this)

    open val shouldShowNoInternetDialog: Boolean = true

    private fun showNoInternetDialog() {
        if (!shouldShowNoInternetDialog) return
        if (isFinishing || isDestroyed) return
        if (noInternetDialog?.isAdded == true) return
        noInternetDialog = NoInternetFullDialog().apply {

        }
        supportFragmentManager.beginTransaction()
            .add(noInternetDialog!!, NoInternetFullDialog::class.simpleName)
            .commitAllowingStateLoss()
    }

    private fun dismissNoInternetDialog() {
        noInternetDialog?.dismissAllowingStateLoss()
        noInternetDialog = null
    }

    open fun onBack() {
        finish()
    }

    override fun attachBaseContext(newBase: Context) {
        language = SpManager.getInstance(this).getLanguage()
        val context = LocaleHelper.setLocale(newBase, language.languageCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(this)
        viewBinding = provideViewBinding()
        setContentView(viewBinding.root)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        hideSystemUI()
        setFullscreen()
        initViews()
        initData()
        initObserver()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        registerNetworkMonitor()
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBack()
        }
    }

    override fun onResume() {
        super.onResume()
        hideNavigationBar()
    }

    fun backPressed(onBack: () -> Unit) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack.invoke()
            }
        })
    }

//    open fun initStatusBarColor(): Int = R.color.colorPrimary

    fun replaceFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(fragment::class.java.simpleName)
            replace(id, fragment, fragment::class.java.simpleName)
        }
    }

    fun addFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(fragment::class.java.simpleName)
            add(id, fragment, fragment::class.java.simpleName)
        }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            show(fragment)
        }
    }

    fun hideFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            hide(fragment)
        }
    }

    fun setFullscreen() {
        applyFitsSystemWindows(viewBinding.root.rootView, false)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }

    private fun hideSystemUI() {
        applyFitsSystemWindows(viewBinding.root.rootView, true)
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }

    private fun applyFitsSystemWindows(view: View?, fitSystemWindows: Boolean) {
        if (view is ViewGroup) {
            ViewCompat.setFitsSystemWindows(view, fitSystemWindows)
            for (i in 0 until view.childCount) {
                applyFitsSystemWindows(view.getChildAt(i), fitSystemWindows)
            }
        }
    }

    abstract fun provideViewBinding(): V

    open fun initViews() {}
    open fun initData() {}
    open fun initObserver() {}

    fun showLoading() {
        loadingDialog.showProgressDialog()
    }

    fun showToast(mes: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, mes, duration).show()
    }

    fun hideLoading() {
        try {
            loadingDialog.hideProgressDialog()
        } catch (e: IllegalArgumentException) {
            Log.e("hideProgressDialog", e.message.toString())
        } catch (e: Exception) {
            Log.e("hideProgressDialog", e.message.toString())
        }
    }

    fun showInterAd(action: () -> Unit) {

    }

    fun showRewardAd(action: () -> Unit) {

    }


    fun hideNavigationBar() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setStatusBarColor(color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
    }

}
