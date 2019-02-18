package com.example.eloem.vertretungsplan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.*
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.activity_current_plan.*
import kotlinx.android.synthetic.main.dialog_meta_data.view.*
import android.support.v4.app.FragmentPagerAdapter


open class CurrentPlanActivity : AppCompatActivity() {
    
    private var mSectionsPagerAdapter: CurrentPlanActivity.SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val darkTheme by booleanPref(SETTINGS_THEME_KEY)
        setTheme(if (darkTheme) R.style.DarkAppTheme_NoActionBar else R.style.AppTheme_NoActionBar)
    
        /** new Theme was applied */
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var recreateParent by booleanPref(RECREATE_PARENT_KEY)
        @Suppress("UNUSED_VALUE")
        recreateParent = false
    
        setContentView(R.layout.activity_current_plan)
    
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    
        setupSwipeRefresh()
        
        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        
        mSectionsPagerAdapter?.onFirstFinish = { onLoadFinish(it) }
    }
    
    override fun onResume() {
        val recreateParent by booleanPref(RECREATE_PARENT_KEY)
        if (recreateParent){
            recreate()
        }
        super.onResume()
    }
    
    open fun setupSwipeRefresh(){
        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            request(true)
        }
    
        container.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                swiperefresh.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }
        
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
        
            override fun onPageSelected(position: Int) {
            }
        })
    }
    
    open fun onLoadFinish(fragments: Int){
        request()
    }
    
    open fun request(forceUpdate: Boolean = false){
        if (planIsUpToDate(this) && !forceUpdate){
            val verPlan = getLatestVerPlanByGrade(this)
            updateChildes(verPlan)
        }else{
            fetchPlan { updateChildes(it) }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_actions_current_plan, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId){
        R.id.settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.deleteTimetable -> {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_deleteTimetable_message)
                    .setPositiveButton(R.string.ok){ _, _ ->
                        insertTimetable(this, Timetable.newDefaultInstance(this))
                    }
                    .setNegativeButton(R.string.cancel){ _, _ ->
                        //do nothing
                    }.show()
            true
        }
        R.id.timetable -> {
            val intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.metaData -> {
            showMetaData(getLatestVerPlanByGrade(this))
            true
        }
        R.id.listVerplan -> {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    @SuppressLint("InflateParams")
    fun showMetaData(verPlan: Vertretungsplan){
        AlertDialog.Builder(this)
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
                .show()
    }
    
    fun updateChildes(plan: Vertretungsplan){
        (mSectionsPagerAdapter?.getFragmentAt(0) as MyPlan?)?.fillContent(plan)
        (mSectionsPagerAdapter?.getFragmentAt(1) as GeneralPlan?)?.fillContent(plan)
        setUpToolbarText(plan)
        swiperefresh.isRefreshing = false
    }
    
    open fun setUpToolbarText(plan: Vertretungsplan){
        toolbar?.title = resources.getString(R.string.actionbar_time_info,
                plan.updateTime.toDate().toTimeString(),
                plan.fetchedTime.toDate().toTimeString())
    }
    
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val registeredFragments = SparseArray<Fragment>()
        
        var onFirstFinish = { _: Int ->}
        private var isFirstFinish = true
    
        override fun getItem(position: Int): Fragment = when(position){
            0 -> MyPlan()
            1 -> GeneralPlan()
            else -> GeneralPlan()
        }
        
        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
    
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }
    
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            registeredFragments.remove(position)
            super.destroyItem(container, position, `object`)
        }
        
        fun getFragmentAt(position: Int): Fragment? = registeredFragments[position]
    
        override fun finishUpdate(container: ViewGroup) {
            super.finishUpdate(container)
            
            if (isFirstFinish){
                onFirstFinish(registeredFragments.size())
                isFirstFinish = false
            }
        }
    }
}