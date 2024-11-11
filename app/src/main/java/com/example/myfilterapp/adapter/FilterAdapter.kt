package com.example.myfilterapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.myfilterapp.R
import com.example.myfilterapp.model.FilterItem
import java.util.*

class FilterAdapter : BaseQuickAdapter<FilterItem, BaseViewHolder>(R.layout.item_filter) {
    companion object {
        const val TYPE_SELECTED = 0
        const val TYPE_OPTIONAL = 1
    }

    private val _selectedItems = mutableListOf<FilterItem>()
    private val _optionalItems = mutableListOf<FilterItem>()

    fun addSelectedItems(items: List<FilterItem>) {
        _selectedItems.addAll(items)
        refreshList()
    }

    fun addOptionalItems(items: List<FilterItem>) {
        _optionalItems.addAll(items)
        refreshList()
    }

    override fun convert(holder: BaseViewHolder, item: FilterItem) {
        val tvTitle = holder.getView<TextView>(R.id.tvTitle)
        tvTitle.text = item.title
        
        when {
            // 标题项样式
            item.id < 0 -> {
                tvTitle.setTextColor(context.getColor(R.color.purple_500))
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                holder.itemView.setBackgroundColor(context.getColor(android.R.color.white))
            }
            // 已选项样式
            item.section == FilterItem.SECTION_SELECTED -> {
                tvTitle.setTextColor(context.getColor(android.R.color.black))
                holder.itemView.setBackgroundColor(context.getColor(android.R.color.white))
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_drag),
                    null,
                    ContextCompat.getDrawable(context, R.drawable.ic_minus),
                    null
                )
            }
            // 可选项样式（包括固定项）
            else -> {
                tvTitle.setTextColor(context.getColor(android.R.color.black))
                holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"))
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_drag),
                    null,
                    ContextCompat.getDrawable(context, R.drawable.ic_plus),
                    null
                )
            }
        }
        
        holder.itemView.setOnClickListener {
            if (item.id > 0) {  // 只有非标题项可以点击
                if (item.section == FilterItem.SECTION_OPTIONAL) {
                    item.section = FilterItem.SECTION_SELECTED
                    _selectedItems.add(item)
                    _optionalItems.remove(item)
                } else {
                    item.section = FilterItem.SECTION_OPTIONAL
                    _optionalItems.add(item)
                    _selectedItems.remove(item)
                }
                refreshList()
            }
        }
    }

    fun refreshList() {
        val newList = ArrayList<FilterItem>(2 + _selectedItems.size + _optionalItems.size)
        newList.add(FilterItem(-1, "已选项", isFixed = true))
        newList.addAll(_selectedItems)
        newList.add(FilterItem(-2, "可选项", isFixed = true))
        newList.addAll(_optionalItems)
        setNewData(newList)
    }

    fun getSelectedItems(): List<FilterItem> = _selectedItems.toList()

    fun moveSelectedItem(fromPosition: Int, toPosition: Int) {
        val selectedStartPos = 1  // 跳过"已选项"标题
        val fromIndex = fromPosition - selectedStartPos
        val toIndex = toPosition - selectedStartPos
        
        if (fromIndex >= 0 && fromIndex < _selectedItems.size &&
            toIndex >= 0 && toIndex < _selectedItems.size) {
            val item = _selectedItems[fromIndex]
            _selectedItems.removeAt(fromIndex)
            _selectedItems.add(toIndex, item)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun moveOptionalItem(fromPosition: Int, toPosition: Int) {
        val optionalStartPos = _selectedItems.size + 2  // 跳过已选项列表和两个标题
        val fromIndex = fromPosition - optionalStartPos
        val toIndex = toPosition - optionalStartPos
        
        if (fromIndex >= 0 && fromIndex < _optionalItems.size &&
            toIndex >= 0 && toIndex < _optionalItems.size) {
            val item = _optionalItems[fromIndex]
            _optionalItems.removeAt(fromIndex)
            _optionalItems.add(toIndex, item)
            notifyItemMoved(fromPosition, toPosition)
        }
    }
} 