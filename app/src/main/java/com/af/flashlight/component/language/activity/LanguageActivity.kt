package com.af.flashlight.component.language.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.language.adapter.LanguageAdapter
import com.af.flashlight.component.language.viewmodel.LanguageViewModel
import com.af.flashlight.component.main.activity.MainActivity
import com.af.flashlight.component.onboarding.activity.OnBoardingActivity
import com.af.flashlight.data.model.Language
import com.af.flashlight.databinding.ActivityLanguageBinding
import com.af.flashlight.utils.Constant
import com.af.flashlight.utils.SpManager
import com.af.flashlight.utils.gone
import com.af.flashlight.utils.setAppLanguage
import com.af.flashlight.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var selectLanguageModel: Language? = null
    private val viewModel: LanguageViewModel by viewModels()
    private val languageAdapter = LanguageAdapter()

    @Inject
    lateinit var spManager: SpManager

    private var isLoadingShowed = false
    private var isFromSplash = false

    override fun provideViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        isFromSplash = intent.getBooleanExtra(Constant.KEY_INTENT_FROM_SPLASH, false)
        toolBar.btnBack.isVisible = !isFromSplash
        toolBar.tvTitle.text = resources.getString(R.string.choose_language)
        toolBar.btnAction.text = resources.getString(R.string.save)

        toolBar.btnAction.visible()
        toolBar.btnAction.isEnabled = !isFromSplash
        lottieView.gone()

        rcvLanguage.adapter = languageAdapter
        languageAdapter.onClick = {
            languageAdapter.selectLanguage(it.languageCode)
            languageAdapter.selectedLanguage()?.let { languageModel ->
                selectLanguageModel = languageModel
            }

            if (isFromSplash && !isLoadingShowed) {
                isLoadingShowed = true
                toolBar.btnAction.gone()
                progressBar.visible()
                lifecycleScope.launch {
                    delay(1200L)
                    progressBar.gone()
                    toolBar.btnAction.isEnabled = true
                    toolBar.btnAction.visible()
                    lottieView.visible() // Stage 2: Trỏ tay lên nút Save
                }
            } else {
                toolBar.btnAction.isEnabled = true
                toolBar.btnAction.visible()
                if (isFromSplash) {
                    lottieView.visible() // Stage 2: Trỏ tay lên nút Save
                }
            }
        }

        toolBar.btnBack.setOnClickListener {
            finish()
        }

        toolBar.btnAction.setOnClickListener {
            progressBar.visible()
            lottieView.gone()
            toolBar.btnAction.gone()
            lifecycleScope.launch {
                delay(1000.milliseconds)
                progressBar.gone()
                selectLanguageModel?.let {
                    spManager.saveLanguage(it)
                    setAppLanguage(it.languageCode)
                    toolBar.btnAction.isEnabled = false
                    if (isFromSplash) {
                        OnBoardingActivity.start(this@LanguageActivity)
                    } else {
                        MainActivity.startNewTask(this@LanguageActivity)
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.loadListLanguage()
    }

    override fun initObserver() {
        viewModel.listLanguage.onEach {
            languageAdapter.setData(ArrayList(it), isFromSplash)
            if (!isFromSplash) {
                languageAdapter.selectLanguage(spManager.getLanguage().languageCode)
                selectLanguageModel = languageAdapter.selectedLanguage()
            }
        }.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).launchIn(lifecycleScope)

    }

    companion object {
        fun start(context: Context, isFromSplash: Boolean) {
            Intent(context, LanguageActivity::class.java).putExtra(Constant.KEY_INTENT_FROM_SPLASH, isFromSplash).also {
                context.startActivity(it)
            }
        }
    }
}
