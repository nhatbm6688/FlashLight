package com.af.flashlight.component.flashalert

import android.os.Bundle
import android.view.ViewGroup
import com.af.flashlight.base.fragment.BaseFragment
import com.af.flashlight.component.flashalert.activity.FlashAlertConfigActivity
import com.af.flashlight.component.flashalert.model.FlashAlertType
import com.af.flashlight.databinding.FragmentFlashAlertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlashAlertFragment : BaseFragment<FragmentFlashAlertBinding>() {

    override fun provideViewBinding(container: ViewGroup?): FragmentFlashAlertBinding {
        return FragmentFlashAlertBinding.inflate(layoutInflater, container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()

        btnIncomingCalls.setOnClickListener {
            FlashAlertConfigActivity.start(requireActivity(), FlashAlertType.CALL)
        }

        btnSms.setOnClickListener {
            FlashAlertConfigActivity.start(requireActivity(), FlashAlertType.SMS)
        }

        btnNotification.setOnClickListener {
            FlashAlertConfigActivity.start(requireActivity(), FlashAlertType.NOTIFICATION)
        }
    }

    companion object {
        fun newInstance(): FlashAlertFragment = FlashAlertFragment()
    }
}
