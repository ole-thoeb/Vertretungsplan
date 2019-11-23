package com.example.eloem.vertretungsplan.ui.currentplan

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.ContextOwner
import com.example.eloem.vertretungsplan.util.toDate
import com.example.eloem.vertretungsplan.util.toWeekdayDateString
import com.example.eloem.vertretungsplan.util.toWeekdayDateTimeString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.dialog_meta_data.view.*
import java.lang.IllegalArgumentException

fun ChildFragment.createPlanPager(): FragmentStateAdapter = PlanPager(PlanPair(MyPlanFragment(), GeneralPlanFragment()), this)

private class PlanPager(private val plans: PlanPair, fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    
    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> plans.cPlan
        1 -> plans.gPlan
        else -> throw IllegalArgumentException("unknown position $position")
    }
}

private data class PlanPair(val cPlan: PlanFragment, val gPlan: PlanFragment)

fun ContextOwner.attachTabMediator(tabLayout: TabLayout, viewPager2: ViewPager2) {
    TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
        tab.text = when(position) {
            0 -> ctx.getString(R.string.tab_text_myPlan)
            1 -> ctx.getString(R.string.tab_text_Plan)
            else -> throw IllegalArgumentException("unknown position $position")
        }
    }.attach()
}

fun ChildFragment.configurViewPager(tabLayout: TabLayout, viewPager2: ViewPager2) {
    viewPager2.adapter = createPlanPager()
    attachTabMediator(tabLayout, viewPager2)
}

@SuppressLint("InflateParams")
fun ChildFragment.showMetaData(verPlan: Vertretungsplan){
    MaterialAlertDialogBuilder(requireContext())
            .setView(layoutInflater.inflate(R.layout.dialog_meta_data, null).apply {
                
                infoRefreshedTV.text = verPlan.updateTime
                        .toDate()
                        .toWeekdayDateTimeString()
                
                infoFetchedTV.text = verPlan.fetchedTime
                        .toDate()
                        .toWeekdayDateTimeString()
                
                infoTargetTV.text = verPlan.targetDay
                        .toDate()
                        .toWeekdayDateString()
            })
            .setTitle(R.string.dialog_metaDate_title)
            .show()
}