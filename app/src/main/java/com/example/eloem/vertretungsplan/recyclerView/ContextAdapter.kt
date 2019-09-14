package com.example.eloem.vertretungsplan.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.eloem.vertretungsplan.util.ContextOwner

abstract class ContextAdapter<VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>(), ContextOwner {
    
    lateinit var recyclerView: RecyclerView
    override val ctx: Context by lazy { recyclerView.context }
    
    val layoutInflater: LayoutInflater by lazy { LayoutInflater.from(ctx) }
    
    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
    }
    
    fun inflate(@LayoutRes resource: Int, parent: ViewGroup): View =
            layoutInflater.inflate(resource, parent, false)
}