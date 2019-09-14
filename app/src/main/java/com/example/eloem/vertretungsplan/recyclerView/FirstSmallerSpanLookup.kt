package com.example.eloem.vertretungsplan.recyclerView

import androidx.recyclerview.widget.GridLayoutManager
import com.example.eloem.vertretungsplan.util.divides

class FirstSmallerSpanLookup(var columns: Int, var ratio: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return if (columns divides position) 1 else ratio
    }
    
    override fun getSpanIndex(position: Int, spanCount: Int): Int {
        require(neededSpans == spanCount) { "actual span count ($spanCount) does not equal needed span count ($neededSpans)!" }
        return (position % columns) * ratio
    }
    
    val neededSpans: Int get() = (columns - 1) * ratio + 1
}