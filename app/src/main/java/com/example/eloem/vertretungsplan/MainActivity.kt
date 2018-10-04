package com.example.eloem.vertretungsplan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.*
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eloem.vertretungsplan.helperClasses.JustTime
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private var mSectionsPagerAdapter: MainActivity.SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    
        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        
        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            request(true)
        }
    
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        
        request(true)
    }
    
    private fun request(forceUpdate: Boolean = false){
        if (planIsUpToDate(this) && !forceUpdate){
            val verPlan = readVertretungsplan(this)
            updateChildes(verPlan)
        }else{
            val queue = Volley.newRequestQueue(this)
            
            val url = getUrl(readGrade(this))
            
            val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        doAsync {
                            val verPlan = Vertretungsplan.newInstance(response,
                                    readTimetable(this@MainActivity))
                            writeVertretungsplan(verPlan, this@MainActivity)
                            writeVerPlanTime(Calendar.getInstance().time, this@MainActivity)
                            uiThread {
                                it.updateChildes(verPlan)
                            }
                        }
                    },
                    Response.ErrorListener { _ ->
                        val verPlan = Vertretungsplan.noConectionPlan()
                        writeVertretungsplan(verPlan, this)
                        updateChildes(verPlan)
                    })
            
            queue.add(stringRequest)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_actions_main, menu)
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
                        writeTimetable(Timetable(4, 10), this)
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
        else -> super.onOptionsItemSelected(item)
    }
    
    private fun updateChildes(plan: Vertretungsplan){
        (mSectionsPagerAdapter?.getFragmentAt(0)as MyPlan).fillContent(plan)
        (mSectionsPagerAdapter?.getFragmentAt(1)as GeneralPlan).fillContent(plan)
        setUpToolbarText(plan)
        swiperefresh.isRefreshing = false
    }
    
    private fun setUpToolbarText(plan: Vertretungsplan){
        val timeRefreshed = readVerPlanTime(this)
        toolbar?.title = "${plan.updateTime} ↻ ${JustTime(timeRefreshed)} \u21A7"
    }
    
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val registeredFragments = SparseArray<Fragment>()
    
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
    }
}