package com.example.eloem.vertretungsplan.ui

import android.content.Context
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.ContextOwner
import com.example.eloem.vertretungsplan.util.readGrade

open class ChildFragment: Fragment(), ContextOwner {
    val hostActivity: HostActivity get() = requireActivity() as HostActivity
    val globalViewModel: GlobalViewModel by activityViewModels()
    val currentGrade: Vertretungsplan.Grade get() = readGrade(requireContext())
    
    override val ctx: Context get() = requireContext()
    
    inline fun configureSupportActionBar(config: ActionBar.() -> Unit) {
        hostActivity.supportActionBar?.apply(config)
    }
    
    inline fun withHost(config: HostActivity.() -> Unit) {
        hostActivity.apply(config)
    }
}