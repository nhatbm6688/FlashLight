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

        updateUiForRating(0)

        ratingBar.onRatingBarChangeListener = OnRatingBarChangeListener { _, rating, _ ->
            this@RateDialog.rating = rating
            updateUiForRating(rating.toInt())
        }

        btnSubmit.setOnClickListener {
            val starCount = this@RateDialog.rating.toInt()
            dismiss()
            if (starCount in 1..3) {
                FeedbackDialog(context).show()
            } else {
                context.openAppInStore()
                Toast.makeText(context, R.string.thanks_for_feedback, Toast.LENGTH_SHORT).show()
            }
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun updateUiForRating(starCount: Int) = with(viewBinding) {
        when (starCount) {
            0 -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_0)
                tvTitle.text = context.getString(R.string.rate_0_title, context.getString(R.string.app_name))
                tvMessage.text = context.getString(R.string.rate_message)
            }

            1 -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_1)
                tvTitle.text = context.getString(R.string.rate_1_title)
                tvMessage.text = context.getString(R.string.rate_1_message)
            }

            2 -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_2)
                tvTitle.text = context.getString(R.string.rate_2_title)
                tvMessage.text = context.getString(R.string.rate_2_message)
            }

            3 -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_3)
                tvTitle.text = context.getString(R.string.rate_3_title)
                tvMessage.text = context.getString(R.string.rate_3_message)
            }

            4 -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_4)
                tvTitle.text = context.getString(R.string.rate_4_title)
                tvMessage.text = context.getString(R.string.rate_4_message)
            }

            else -> {
                ivRatingEmotion.setImageResource(R.mipmap.ic_ratting_5)
                tvTitle.text = context.getString(R.string.rate_5_title)
                tvMessage.text = context.getString(R.string.rate_5_message)
            }
        }
        btnSubmit.setText(R.string.submit)
    }
}
