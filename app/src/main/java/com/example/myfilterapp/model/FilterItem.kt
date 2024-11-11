package com.example.myfilterapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterItem(
    val id: Int,
    val title: String,
    var isSelected: Boolean = false,
    var isFixed: Boolean = false,
    var section: Int = SECTION_OPTIONAL
) : Parcelable {
    companion object {
        const val SECTION_SELECTED = 0
        const val SECTION_OPTIONAL = 1
    }
}

data class SubFilterItem(
    val id: Int,
    val title: String,
    var isSelected: Boolean = false,
    var isFixed: Boolean = false
) 