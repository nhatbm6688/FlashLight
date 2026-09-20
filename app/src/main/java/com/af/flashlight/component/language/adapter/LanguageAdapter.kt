package com.af.flashlight.component.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.flashlight.R
import com.af.flashlight.base.adapter.BaseAdapter
import com.af.flashlight.data.model.Language
import com.af.flashlight.databinding.ItemLanguageBinding
import com.af.flashlight.utils.gone
import com.af.flashlight.utils.visible

class LanguageAdapter : BaseAdapter<Language, ItemLanguageBinding>() {
    private var isFromSplash = false

    fun setData(listItem: ArrayList<Language>, isFromSplash: Boolean) {
        this.isFromSplash = isFromSplash
        super.setData(listItem)
    }

    override fun provideViewBinding(parent: ViewGroup): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun binData(viewBinding: ItemLanguageBinding, item: Language, position: Int) {
        viewBinding.apply {
            tvTitle.setText(item.nameRes)
            if (item.languageCode == "en" && isFromSplash && selectedLanguage() == null) {
                lottieView.visible()
            } else {
                lottieView.gone()
            }
            if (item.selected) {
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
            } else {
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_normal)
            }
            root.setOnClickListener {
                lottieView.gone()
                onClick?.invoke(item)
            }

        }
    }

    fun selectLanguage(languageCode: String) {
        var index = dataSet.indexOfFirst { it.selected }
        if (index > -1) {
            dataSet[index].selected = false
            notifyItemChanged(index)
        }
        index = dataSet.indexOfFirst { it.languageCode == languageCode }
        if (index > -1) {
            dataSet[index].selected = true
            notifyItemChanged(index)
        }
        if (isFromSplash) {
            val enIndex = dataSet.indexOfFirst { it.languageCode == "en" }
            if (enIndex > -1 && enIndex != index) {
                notifyItemChanged(enIndex)
            }
        }
    }

    fun selectedLanguage(): Language? = dataSet.firstOrNull { it.selected }
}
