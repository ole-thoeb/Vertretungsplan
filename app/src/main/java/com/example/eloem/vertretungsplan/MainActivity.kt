package com.example.eloem.vertretungsplan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.*
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.eloem.vertretungsplan.helperClasses.JustTime
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.util.readVerPlanTime
import com.example.eloem.vertretungsplan.util.readVertretungsplan
import com.example.eloem.vertretungsplan.util.writeTimetable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    
    private var mSectionsPagerAdapter: MainActivity.SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    
        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
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
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener(){ _, _ ->
                        writeTimetable(Timetable(4, 10), this)
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener(){ _, _ ->
                        //do nothing
                    }).show()
            true
        }
        R.id.timetable -> {
            val intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    fun updateOtherChild(isMyPlan: Boolean){
        val verPlan = readVertretungsplan(this)
        if (isMyPlan){
            val page = supportFragmentManager.findFragmentByTag("android:switcher:${R.id.container}:1")
            (page as Plan).fillContent(verPlan, false)
        }else{
            val page = supportFragmentManager.findFragmentByTag("android:switcher:${R.id.container}:0")
            (page as Plan).fillContent(verPlan,false)
        }
    }
    
    fun setUpToolbarText(){
        val plan = readVertretungsplan(this)
        val timeRefreshed = readVerPlanTime(this)
        toolbar?.title = "${plan.getUpdateTime()} ↻ ${JustTime(timeRefreshed)} \u21A7"
    }
    
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        
        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            if(position == 0){
                return Plan.newInstance(true)
            }
            return Plan.newInstance(false)
        }
        
        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
    }
}