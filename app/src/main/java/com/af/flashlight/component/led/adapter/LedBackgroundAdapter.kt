package com.af.flashlight.component.led.adapter

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
            val isSelected = position == selectedPosition && !item.isAddButton
            viewSelectionBorder.isSelected = isSelected

            if (item.isAddButton) {
                ivThumbnail.setImageDrawable(null)
                ivAddIcon.visibility = View.VISIBLE
                viewSelectionBorder.visibility = View.GONE
            } else {
                ivAddIcon.visibility = View.GONE
                viewSelectionBorder.visibility = View.VISIBLE

                if (item.customUri != null) {
                    ivThumbnail.setImageURI(item.customUri)
                } else if (item.resId != 0) {
                    ivThumbnail.setImageResource(item.resId)
                } else {
                    ivThumbnail.setImageDrawable(null)
                }
            }

            root.setOnClickListener {
                if (item.isAddButton) {
                    onAddClicked()
                } else {
                    val prev = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(prev)
                    notifyItemChanged(selectedPosition)
                    onBackgroundSelected(item, position)
                }
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
     * Insert a new custom image at [insertedPosition] and optionally
     * remove the stale one at [removedPosition] (if >= 0).
     */
    fun applyAddResult(insertedPosition: Int, removedPosition: Int) {
        // Remove old item first (index shifts haven't happened yet)
        if (removedPosition >= 0) {
            items.removeAt(removedPosition)
            notifyItemRemoved(removedPosition)
        }
        // Insert new item
        val prev = selectedPosition
        selectedPosition = insertedPosition
        notifyItemChanged(prev)
        notifyItemInserted(insertedPosition)
    }
}
