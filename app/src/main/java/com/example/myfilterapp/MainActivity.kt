package com.example.myfilterapp

import DataAdapter
import DataItem
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var llFilterContainer: LinearLayout
    private lateinit var adapter: DataAdapter
    private var selectedItems: ArrayList<String>? = null
    private var allData = mutableListOf<DataItem>()
    
    private val filterLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedItems = result.data?.getStringArrayListExtra("SELECTED_ITEMS")
            selectedItems?.let { items ->
                llFilterContainer.removeAllViews()
                items.forEach { title ->
                    val itemView = layoutInflater.inflate(R.layout.layout_filter_item, llFilterContainer, false)
                    itemView.findViewById<TextView>(R.id.tvLabel).text = title
                    itemView.findViewById<EditText>(R.id.etValue).hint = "请输入${title}"
                    llFilterContainer.addView(itemView)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        llFilterContainer = findViewById(R.id.llFilterContainer)
        setupRecyclerView()
        setupSearchButton()
        setupFilterButton()
        setupDefaultFilters()
        loadMockData()
    }
    
    private fun setupFilterButton() {
        val btnFilter = findViewById<Button>(R.id.btnFilter)
        btnFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            intent.putStringArrayListExtra("SELECTED_ITEMS", selectedItems)
            filterLauncher.launch(intent)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = DataAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.rvData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupSearchButton() {
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            // 获取所有筛选条件的值
            val filterValues = mutableMapOf<String, String>()
            for (i in 0 until llFilterContainer.childCount) {
                val itemView = llFilterContainer.getChildAt(i)
                val title = itemView.findViewById<TextView>(R.id.tvLabel).text.toString()
                val value = itemView.findViewById<EditText>(R.id.etValue).text.toString()
                filterValues[title] = value
            }
            
            // 根据筛选条件过滤数据
            val filteredList = allData.filter { item ->
                filterValues.all { (title, value) ->
                    if (value.isEmpty()) return@all true
                    when (title) {
                        "单据类型" -> item.type.contains(value)
                        "单据状态" -> item.status.contains(value)
                        "销售员" -> item.salesman.contains(value)
                        "日期" -> item.date.contains(value)
                        "部门" -> item.department.contains(value)
                        "负责人" -> item.owner.contains(value)
                        "单据来源" -> item.source.contains(value)
                        "归属地" -> item.location.contains(value)
                        else -> true
                    }
                }
            }
            adapter.setNewData(filteredList)
        }
    }
    
    private fun loadMockData() {
        // 添加模拟数据
        allData = MutableList(20) { index ->
            DataItem(
                id = index,
                title = "单据 #${index + 1}",
                description = "这是一个测试单据",
                type = "类型${index % 3 + 1}",
                status = "状态${index % 4 + 1}",
                salesman = "销售员${index % 5 + 1}",
                date = "2024-03-${index % 30 + 1}",
                department = "部门${index % 4 + 1}",
                owner = "负责人${index % 6 + 1}",
                source = "来源${index % 3 + 1}",
                location = "地区${index % 5 + 1}"
            )
        }
        adapter.setNewData(allData)
    }
    
    private fun setupDefaultFilters() {
        // 设置默认的筛选条件
        selectedItems = arrayListOf("单据类型", "单据状态", "销售员")
        selectedItems?.let { items ->
            llFilterContainer.removeAllViews()
            items.forEach { title ->
                val itemView = layoutInflater.inflate(R.layout.layout_filter_item, llFilterContainer, false)
                itemView.findViewById<TextView>(R.id.tvLabel).text = title
                itemView.findViewById<EditText>(R.id.etValue).hint = "请输入${title}"
                llFilterContainer.addView(itemView)
            }
        }
    }
}