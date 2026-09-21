package com.af.flashlight.component.onboarding.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.af.flashlight.data.model.OnBoarding
import com.af.flashlight.databinding.ItemOnBoardingFullBinding
import com.af.flashlight.databinding.ItemOnBoardingNativeAdBinding

class OnBoardingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataSet = ArrayList<OnBoarding>()

    companion object {
        private const val TYPE_NATIVE_AD = 0
        private const val TYPE_FULL = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == 2) TYPE_NATIVE_AD else TYPE_FULL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_NATIVE_AD) {
            NativeAdViewHolder(ItemOnBoardingNativeAdBinding.inflate(inflater, parent, false))
        } else {
            FullViewHolder(ItemOnBoardingFullBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        when (holder) {
            is NativeAdViewHolder -> holder.bind(item)
            is FullViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(items: List<OnBoarding>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    class NativeAdViewHolder(private val binding: ItemOnBoardingNativeAdBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnBoarding) {
            binding.apply {
                tvTitle.text = root.context.resources.getString(item.title)
                tvDescription.text = root.context.resources.getString(item.description)
                imgBoarding.setImageResource(item.imageId)
            }
        }
    }

    class FullViewHolder(private val binding: ItemOnBoardingFullBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnBoarding) {
            binding.apply {
                tvTitle.text = root.context.resources.getString(item.title)
                tvDescription.text = root.context.resources.getString(item.description)
                imgBoarding.setImageResource(item.imageId)
            }
        }
    }
}
