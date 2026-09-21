package com.af.flashlight.component.onboarding.activity

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.main.activity.MainActivity
import com.af.flashlight.component.permission.PermissionActivity
import com.af.flashlight.component.onboarding.adpater.OnBoardingAdapter
import com.af.flashlight.component.onboarding.viewmodel.OnBoardingViewModel
import com.af.flashlight.databinding.ActivityOnBoardingBinding
import com.af.flashlight.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val onBoardingAdapter = OnBoardingAdapter()
    private val viewModels: OnBoardingViewModel by viewModels()

    override fun provideViewBinding(): ActivityOnBoardingBinding = ActivityOnBoardingBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setFullscreen()

        vpOnBoarding.adapter = onBoardingAdapter
        dotsIndicator.attachTo(vpOnBoarding)

        updateNavigationPosition(0)

        vpOnBoarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                interpolateNavigationPosition(position, positionOffset)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModels.currentPosition = position
                btnNext.text = getString(R.string.next)
                updateNavigationPosition(position)
            }
        })

        btnNext.setOnClickListener {
            nextAction()
        }
    }

    private fun getNavigationMarginForPage(page: Int): Int {
        val isNativeAdPage = (page == 0 || page == 2)
        return if (isNativeAdPage) {
            resources.getDimensionPixelSize(R.dimen.size250)
        } else {
            resources.getDimensionPixelSize(R.dimen.size65)
        }
    }

    private fun updateNavigationPosition(position: Int) {
        val bottomMargin = getNavigationMarginForPage(position)
        (viewBinding.clNavigation.layoutParams as? ViewGroup.MarginLayoutParams)?.let { params ->
            if (params.bottomMargin != bottomMargin) {
                params.bottomMargin = bottomMargin
                viewBinding.clNavigation.layoutParams = params
            }
        }
    }

    private fun interpolateNavigationPosition(position: Int, positionOffset: Float) {
        val currentMargin = getNavigationMarginForPage(position)
        val nextMargin = if (position + 1 < onBoardingAdapter.dataSet.size) {
            getNavigationMarginForPage(position + 1)
        } else {
            currentMargin
        }
        val targetMargin = (currentMargin + (nextMargin - currentMargin) * positionOffset).toInt()
        (viewBinding.clNavigation.layoutParams as? ViewGroup.MarginLayoutParams)?.let { params ->
            params.bottomMargin = targetMargin
            viewBinding.clNavigation.layoutParams = params
        }
    }

    private fun nextAction() {
        if (viewModels.currentPosition < onBoardingAdapter.dataSet.size - 1) {
            viewBinding.vpOnBoarding.setCurrentItem(viewModels.currentPosition + 1, true)
        } else {
            PermissionActivity.start(this@OnBoardingActivity)
            finish()
        }
    }

    override fun initData() {
        super.initData()
        viewModels.getListOnBoarding()
    }

    override fun initObserver() {
        super.initObserver()
        viewModels.listOnBoarding.onEach {
            onBoardingAdapter.setData(ArrayList(it))
        }.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).launchIn(lifecycleScope)
    }

    companion object {
        fun start(context: Context) {
            Intent(context, OnBoardingActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
