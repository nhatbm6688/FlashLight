package com.af.flashlight.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.af.flashlight.R
import com.af.flashlight.base.dialog.BaseDialog
import com.af.flashlight.databinding.DialogFeedbackBinding

class FeedbackDialog(private val context: Context) : BaseDialog<DialogFeedbackBinding>(context) {

    override fun provideViewBinding(): DialogFeedbackBinding {
        return DialogFeedbackBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(true)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSend.setOnClickListener {
            val content = etFeedback.text?.toString()?.trim() ?: ""
            if (content.isEmpty()) {
                Toast.makeText(context, R.string.feedback_empty_warning, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dismiss()
            sendFeedbackEmail(content)
            Toast.makeText(context, R.string.thanks_for_feedback, Toast.LENGTH_SHORT).show()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun sendFeedbackEmail(content: String) {
        val email = "nhatbm.dev@aficaglobal.com"
        val subject = "[Flashlight] User Feedback"
        val mailtoUri = Uri.parse("mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(content)}")
        val emailIntent = Intent(Intent.ACTION_SENDTO, mailtoUri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        try {
            val chooser = Intent.createChooser(emailIntent, context.getString(R.string.share))
            if (context !is Activity) {
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, content)
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                val chooser = Intent.createChooser(fallbackIntent, context.getString(R.string.share))
                if (context !is Activity) {
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
