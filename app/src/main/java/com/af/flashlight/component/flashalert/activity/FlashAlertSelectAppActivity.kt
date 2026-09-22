package com.af.flashlight.component.flashalert.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.af.flashlight.R
import com.af.flashlight.base.activity.BaseActivity
import com.af.flashlight.component.flashalert.adapter.SelectAppAdapter
import com.af.flashlight.component.flashalert.viewmodel.FlashAlertViewModel
import com.af.flashlight.databinding.ActivityFlashAlertSelectAppBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashAlertSelectAppActivity : BaseActivity<ActivityFlashAlertSelectAppBinding>() {

    private val viewModel: FlashAlertViewModel by viewModels()
    private lateinit var selectAppAdapter: SelectAppAdapter

    override fun provideViewBinding(): ActivityFlashAlertSelectAppBinding =
        ActivityFlashAlertSelectAppBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        toolBar.tvTitle.text = getString(R.string.select_app)
        toolBar.btnBack.setOnClickListener { finish() }
        toolBar.btnAction.visibility = View.VISIBLE
        toolBar.btnAction.text = getString(R.string.select_all)

        selectAppAdapter = SelectAppAdapter { appItem ->
            viewModel.toggleAppSelection(appItem.packageName)
        }

        rvApps.apply {
            layoutManager = LinearLayoutManager(this@FlashAlertSelectAppActivity)
            adapter = selectAppAdapter
            setHasFixedSize(true)
        }

        toolBar.btnAction.setOnClickListener {
            val currentList = viewModel.uiState.value.installedApps
            val allSelected = currentList.isNotEmpty() && currentList.all { it.isSelected }
            viewModel.selectAllApps(!allSelected)
        }

        btnDone.setOnClickListener {
            viewModel.saveSelectedApps()
            finish()
        }

        viewModel.loadInstalledApps()
    }

    override fun initObserver() {
        super.initObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    viewBinding.progressBar.isVisible = state.isLoadingApps
                    selectAppAdapter.submitList(state.installedApps)

                    val allSelected = state.installedApps.isNotEmpty() && state.installedApps.all { it.isSelected }
                    viewBinding.toolBar.btnAction.text = getString(
                        if (allSelected) R.string.deselect_all else R.string.select_all
                    )
                }
            }
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, FlashAlertSelectAppActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
