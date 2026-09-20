package com.af.flashlight.dialog

import android.content.Context
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import com.af.flashlight.R
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogRateBinding
import com.af.flashlight.utils.openAppInStore

class RateDialog(private val context: Context) : BaseDialog<DialogRateBinding>(context) {
    private var rating = 0f

    override fun provideViewBinding(): DialogRateBinding {
        return DialogRateBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(true)

        tvTitle.text = context.getString(R.string.rate_title, context.getString(R.string.app_name))

        ratingBar.onRatingBarChangeListener = OnRatingBarChangeListener { _, rating, _ ->
            this@RateDialog.rating = rating
            when (rating.toInt()) {
                1 -> {
                    tvTitle.text = context.resources.getString(R.string.rate_1_title)
                    tvMessage.text = context.resources.getString(R.string.rate_1_message)
                    btnRate.text = context.resources.getString(R.string.rate_1_button)
                }

                2 -> {
                    tvTitle.text = context.resources.getString(R.string.rate_2_title)
                    tvMessage.text = context.resources.getString(R.string.rate_2_message)
                    btnRate.text = context.resources.getString(R.string.rate_2_button)
                }

                3 -> {
                    tvTitle.text = context.resources.getString(R.string.rate_3_title)
                    tvMessage.text = context.resources.getString(R.string.rate_3_message)
                    btnRate.text = context.resources.getString(R.string.rate_3_button)
                }

                4 -> {
                    tvTitle.text = context.resources.getString(R.string.rate_4_title)
                    tvMessage.text = context.resources.getString(R.string.rate_4_message)
                    btnRate.text = context.resources.getString(R.string.rate_4_button)
                }

                else -> {
                    tvTitle.text = context.resources.getString(R.string.rate_5_title)
                    tvMessage.text = context.resources.getString(R.string.rate_5_message)
                    btnRate.text = context.resources.getString(R.string.rate_5_button)
                }
            }
        }

        btnRate.setOnClickListener {
            dismiss()
            if (rating < 4) {
                Toast.makeText(context, context.resources.getString(R.string.thanks_for_feedback), Toast.LENGTH_SHORT).show()
            } else {
                context.openAppInStore()
            }
        }

        btnCancel.setOnClickListener { dismiss() }

    }

}
