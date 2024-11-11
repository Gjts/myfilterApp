package com.example.myfilterapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfilterapp.adapter.FilterAdapter
import com.example.myfilterapp.model.FilterItem
import java.util.Collections

class FilterActivity : AppCompatActivity() {
    private lateinit var adapter: FilterAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        
        setupRecyclerView()
        setupData()
        
        setupSaveButton()
    }
    
    private fun setupRecyclerView() {
        adapter = FilterAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilter)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        var dragFrom = -1
        var dragTo = -1
        
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = source.bindingAdapterPosition
                val toPos = target.bindingAdapterPosition
                
                val fromItem = adapter.data.getOrNull(fromPos) ?: return false
                val toItem = adapter.data.getOrNull(toPos) ?: return false
                
                // 不允许拖拽标题项
                if (fromItem.id < 0 || toItem.id < 0) {
                    return false
                }
                
                // 确保在同一区域内拖拽
                if (fromItem.section == toItem.section) {
                    if (dragFrom == -1) {
                        dragFrom = fromPos
                    }
                    dragTo = toPos
                    
                    if (fromItem.section == FilterItem.SECTION_SELECTED) {
                        adapter.moveSelectedItem(fromPos, toPos)
                    } else {
                        adapter.moveOptionalItem(fromPos, toPos)
                    }
                    return true
                }
                return false
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
                viewHolder.itemView.scaleX = 1.0f
                viewHolder.itemView.scaleY = 1.0f
                
                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    dragFrom = -1
                    dragTo = -1
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.animate()
                        ?.alpha(0.7f)
                        ?.scaleX(1.1f)
                        ?.scaleY(1.1f)
                        ?.setDuration(100)
                        ?.start()
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }
    
    private fun setupData() {
        val savedSelectedItems = intent.getStringArrayListExtra("SELECTED_ITEMS")
        
        // 初始化固定的已选项，添加 isFixed 属性
        val fixedItems = listOf(
            FilterItem(1, "单据类型", isFixed = true, section = FilterItem.SECTION_SELECTED),
            FilterItem(2, "单据状态", isFixed = true, section = FilterItem.SECTION_SELECTED),
            FilterItem(3, "销售员", isFixed = true, section = FilterItem.SECTION_SELECTED)
        )
        
        // 初始化可选项
        val allOptionalItems = listOf(
            FilterItem(4, "日期", section = FilterItem.SECTION_OPTIONAL),
            FilterItem(5, "部门", section = FilterItem.SECTION_OPTIONAL),
            FilterItem(6, "负责人", section = FilterItem.SECTION_OPTIONAL),
            FilterItem(7, "单据来源", section = FilterItem.SECTION_OPTIONAL),
            FilterItem(8, "归属地", section = FilterItem.SECTION_OPTIONAL)
        )
        
        if (savedSelectedItems != null) {
            // 处理固定项的状态，保持 isFixed 属性
            val selectedFixedItems = fixedItems.map { item ->
                if (savedSelectedItems.contains(item.title)) {
                    item.copy(isFixed = true, section = FilterItem.SECTION_SELECTED)
                } else {
                    item.copy(isFixed = true, section = FilterItem.SECTION_OPTIONAL)
                }
            }
            
            // 处理可选项的状态
            val selectedOptionalItems = allOptionalItems.filter { item -> 
                savedSelectedItems.contains(item.title)
            }.map { it.copy(section = FilterItem.SECTION_SELECTED) }
            
            val remainingOptionalItems = (allOptionalItems + selectedFixedItems.filter { 
                !savedSelectedItems.contains(it.title) 
            }).filter { item ->
                !selectedOptionalItems.any { it.title == item.title }
            }
            
            adapter.addSelectedItems(selectedFixedItems.filter { it.section == FilterItem.SECTION_SELECTED } + selectedOptionalItems)
            adapter.addOptionalItems(remainingOptionalItems)
        } else {
            adapter.addSelectedItems(fixedItems)
            adapter.addOptionalItems(allOptionalItems)
        }
    }
    
    private fun setupSaveButton() {
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            val intent = Intent().apply {
                putParcelableArrayListExtra("SELECTED_ITEMS_PARCELABLE", ArrayList(selectedItems))
                putStringArrayListExtra("SELECTED_ITEMS", ArrayList(selectedItems.map { it.title }))
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
} 