package com.af.flashlight.component.led.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.af.flashlight.component.led.model.LedBackgroundItem
import com.af.flashlight.databinding.ItemLedBackgroundBinding

class LedBackgroundAdapter(
    items: List<LedBackgroundItem>,
    private val onAddClicked: () -> Unit,
    private val onBackgroundSelected: (LedBackgroundItem, Int) -> Unit
) : RecyclerView.Adapter<LedBackgroundAdapter.ViewHolder>() {

    private val items: MutableList<LedBackgroundItem> = items.toMutableList()

    var selectedPosition: Int = 1
        private set

    inner class ViewHolder(private val binding: ItemLedBackgroundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LedBackgroundItem, position: Int) = with(binding) {
            val hasCustomImage = item.isAddButton && item.customUri != null
            val isAddSlotEmpty = item.isAddButton && item.customUri == null

            val isSelected = position == selectedPosition && !isAddSlotEmpty
            viewSelectionBorder.isSelected = isSelected

            if (isAddSlotEmpty) {
                // Chưa có ảnh tải lên: Hiển thị icon dấu cộng, không có viền chọn
                ivThumbnail.setImageDrawable(null)
                ivAddIcon.visibility = View.VISIBLE
                viewSelectionBorder.visibility = View.GONE
            } else {
                // Đã có ảnh tải lên đè lên dấu cộng HOẶC là preset background:
                // Dấu cộng ẩn đi, hiển thị ảnh và viền chọn
                ivAddIcon.visibility = View.GONE
                viewSelectionBorder.visibility = View.VISIBLE

                if (item.customUri != null) {
                    ivThumbnail.setImageURI(null)
                    ivThumbnail.setImageURI(item.customUri)
                } else if (item.resId != 0) {
                    ivThumbnail.setImageResource(item.resId)
                } else {
                    ivThumbnail.setImageDrawable(null)
                }
            }

            root.setOnClickListener {
                if (isAddSlotEmpty) {
                    // Chưa có ảnh -> bấm vào để tải ảnh lên từ điện thoại
                    onAddClicked()
                } else if (hasCustomImage) {
                    // Đã có ảnh đè lên dấu cộng:
                    if (selectedPosition == position) {
                        // Nếu đang chọn ô này mà nhấn tiếp -> mở thư viện để đổi ảnh khác
                        onAddClicked()
                    } else {
                        // Nếu đang chọn preset khác -> nhấn vào để chọn lại ảnh này
                        val prev = selectedPosition
                        selectedPosition = position
                        notifyItemChanged(prev)
                        notifyItemChanged(selectedPosition)
                        onBackgroundSelected(item, position)
                    }
                } else {
                    // Preset background thông thường
                    val prev = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(prev)
                    notifyItemChanged(selectedPosition)
                    onBackgroundSelected(item, position)
                }
            }

            // Nhấn giữ vào ô custom image cũng mở picker để đổi ảnh nhanh
            root.setOnLongClickListener {
                if (hasCustomImage) {
                    onAddClicked()
                    true
                } else false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLedBackgroundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Restore full item list from ViewModel (called on Fragment resume).
     * [newSelectedPosition] is the previously saved selected position.
     */
    fun restoreItems(newItems: List<LedBackgroundItem>, newSelectedPosition: Int) {
        items.clear()
        items.addAll(newItems)
        selectedPosition = newSelectedPosition
        notifyDataSetChanged()
    }

    /**
     * Updates the single custom background image at index 0 and selects it.
     */
    fun setCustomBackground(uri: Uri) {
        if (items.isNotEmpty()) {
            items[0] = items[0].copy(customUri = uri)
            val prev = selectedPosition
            selectedPosition = 0
            if (prev != 0) notifyItemChanged(prev)
            notifyItemChanged(0)
        }
    }
}
