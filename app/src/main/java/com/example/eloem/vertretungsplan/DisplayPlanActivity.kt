package com.example.eloem.vertretungsplan

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.getVerPlan
import com.example.eloem.vertretungsplan.util.toDate
import com.example.eloem.vertretungsplan.util.toWeekdayDateTimeString
import kotlinx.android.synthetic.main.activity_current_plan.*

class DisplayPlanActivity : CurrentPlanActivity() {
    
    private var planFetchedTime = -1L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planFetchedTime = intent.getLongExtra(EXTRA_PLAN_FETCHED_TIME, -1)
        container.transitionName = planFetchedTime.toString()
        
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    
    override fun request(forceUpdate: Boolean) {
        val plan = getVerPlan(this, planFetchedTime)
        updateChildes(plan)
    }
    
    override fun setupSwipeRefresh() {
        // Want no swipeRefresh
        swiperefresh.isEnabled = false
    }
    
    override fun onLoadFinish(fragments: Int) {
        super.onLoadFinish(fragments)
        container.currentItem = 1
    }
    
    override fun setUpToolbarText(plan: Vertretungsplan) {
        toolbar.title = plan.updateTime.toDate().toWeekdayDateTimeString()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_actions_display_plan, menu)
        return true
    }
    
    override fun onBackPressed() {
        if(container.currentItem != 1 || resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish()
        }else finishAfterTransition()
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId){
        R.id.metaData -> {
            showMetaData(getVerPlan(this, planFetchedTime))
            true
        }
        /*R.id.currentPLan -> {
            startActivity(Intent(this, CurrentPlanActivity::class.java))
            true
        }*/
        else -> super.onOptionsItemSelected(item)
    }
    
    companion object {
        const val EXTRA_PLAN_FETCHED_TIME = "planFetchedTime"
    }
}
