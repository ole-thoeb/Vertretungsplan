package com.example.eloem.vertretungsplan

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.*
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.card_vertretungsplan.view.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.plan_row.view.*
import kotlin.math.log10

class ListActivity : AppCompatActivity() {
    private var currentlyCABActive = false
    
    
    private var efEnabled by booleanPref(FILTER_EF_ENABLED_KEY, true)
    private var q1Enabled by booleanPref(FILTER_Q1_ENABLED_KEY, true)
    private var q2Enabled by booleanPref(FILTER_Q2_ENABLED_KEY, true)
    private var forMe by booleanPref(FILTER_FOR_ME_KEY)
    private var seekerProgress by intPref(FILTER_SEEKER_PROGRESS_KEY, 100)
    private var lastOfDay by booleanPref(FILTER_LAST_OF_DAY_KEY)
    
    private lateinit var mAdapter: ListAdapter
    private val filteredPlans: List<Vertretungsplan> get() {
        val enabledGrades = mutableListOf<String>()
        if (!efEnabled && !q1Enabled && !q2Enabled) enabledGrades.addAll(arrayOf("EF", "Q1", "Q2"))
        else{
            if (efEnabled) enabledGrades.add("EF")
            if (q1Enabled) enabledGrades.add("Q1")
            if (q2Enabled) enabledGrades.add("Q2")
        }
        
        val latestTime = if (seekerProgress == 100) 0
                else System.currentTimeMillis() - progressToDays(seekerProgress) * 1000 * 60 * 60* 24
        
        val filteredPlans = getAllVerPlans(this).filter {
            (!forMe || it.customPlan.plan.isNotEmpty()) &&
                    it.grade in enabledGrades && it.updateTime > latestTime
        }
        if (lastOfDay) {
            val groupedPlans = filteredPlans.groupBy {
                it.grade.toIntCharByChar().toLong() + it.targetDay
            }.values.toList()
            return List(groupedPlans.size) {
                groupedPlans[it].maxBy { plan ->
                    plan.fetchedTime
                }!!
            }
        }
        
        return filteredPlans
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val darkTheme by booleanPref(SETTINGS_THEME_KEY)
        setTheme(if (darkTheme) R.style.DarkAppTheme else R.style.AppTheme)
    
        setContentView(R.layout.activity_list)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        mAdapter = ListAdapter(filteredPlans, this)
        var numberOfSelectedViews = 0
        with(verPlanList) {
            adapter = mAdapter
            choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
            setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
                override fun onItemCheckedStateChanged(mode: ActionMode, position: Int,
                                                       id: Long, checked: Boolean) {
                    // Here you can do something when items are selected/de-selected,
                    // such as update the title in the CAB
                    if (checked) {
                        numberOfSelectedViews++
                        //mAdapter.setNewSelection(position, checked);
                    } else {
                        numberOfSelectedViews--
                        //mAdapter.removeSelection(position);
                    }
                    mode.title = resources.getString(R.string.titleCAB, numberOfSelectedViews)
                }
        
                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    // Respond to clicks on the actions in the CAB
                    return when (item.itemId) {
                        R.id.deletePlan -> {
                            mAdapter.selectedPlans.forEach { deleteVertretungsPlan(this@ListActivity, it) }
                            refreshList()
                            mode.finish() // Action picked, so close the CAB
                            true
                        }
                        else -> false
                    }
                }
        
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    // Inflate the menu for the CAB
                    val menuInflater = mode.menuInflater
                    menuInflater.inflate(R.menu.list_context, menu)
                    currentlyCABActive = true
                    return true
                }
        
                override fun onDestroyActionMode(mode: ActionMode) {
                    // Here you can make any necessary updates to the activity when
                    // the CAB is removed. By default, selected items are deselected/unchecked.
                    numberOfSelectedViews = 0
                    currentlyCABActive = false
    
                    mAdapter.removeSelections()
                }
        
                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    // Here you can perform updates to the CAB due to
                    // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                    return false
                }
            })
        }
    }
    
    override fun onActionModeStarted(mode: ActionMode?) {
        super.onActionModeStarted(mode)
        window.statusBarColor = resources.getColor(R.color.darkAccentColor, theme)
    }
    
    override fun onActionModeFinished(mode: ActionMode?) {
        super.onActionModeFinished(mode)
        //set statusBar color back to normal
        window.statusBarColor = getAttribute(R.attr.colorPrimaryDark, true).data
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_action_list, menu)
        return true
    }
    
    private fun refreshList(){
        mAdapter.plans = filteredPlans
        mAdapter.notifyDataSetChanged()
    }
    
    private fun progressToDays(progress: Int) = if (progress == 0) 1
        else 2 + (0.3 * progress * log10(progress.toFloat())).toInt()
    
    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId){
        R.id.listFilter -> {
            val custView = layoutInflater.inflate(R.layout.dialog_filter, null).apply {
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val days = progressToDays(progress)
                        periodTV.text = if (progress == 100) resources.getString(R.string.periodDefault)
                        else resources.getQuantityString(R.plurals.periodText, days, days)
                    }
        
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }
        
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
                seekBar.progress = seekerProgress
                EFCB.isChecked = efEnabled
                Q1CB.isChecked = q1Enabled
                Q2CB.isChecked = q2Enabled
                forMeCB.isChecked = forMe
                lastOfDayCB.isChecked = lastOfDay
            }
            AlertDialog.Builder(this)
                    .setView(custView)
                    .setTitle(R.string.dialog_filterTitle)
                    .setPositiveButton(R.string.dialog_filterPositive) { _, _->
                        with(custView) {
                            seekerProgress = seekBar.progress
                            efEnabled = EFCB.isChecked
                            q1Enabled = Q1CB.isChecked
                            q2Enabled = Q2CB.isChecked
                            forMe = forMeCB.isChecked
                            lastOfDay = lastOfDayCB.isChecked
                        }
                        refreshList()
                    }
                    /*.setNeutralButton(R.string.cancel) { dialog, which ->
                    
                    }*/
                    .setNegativeButton(R.string.dialog_filterNegative) { _, _ ->
                        seekerProgress = 100
                        efEnabled = true
                        q1Enabled = true
                        q2Enabled = true
                        forMe = false
                        lastOfDay = false
                        refreshList()
                    }
                    .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    inner class ListAdapter(var plans: List<Vertretungsplan>, private val context: Context): BaseAdapter() {
        
        private val selectedViews: MutableMap<Int, Boolean> = emptyMap<Int, Boolean>().toMutableMap()
        
        val selectedPlans get() = plans.subList(selectedViews.toList().copyOf { it.first })
        
        fun itemInCABPressed(position: Int) {
            if (isItemChecked(position)) deselectItem(position)
            else selectItem(position)
        }
        
        fun selectItem(position: Int){
            verPlanList.setItemChecked(position, true)
            selectedViews[position] =  true
        }
        
        fun deselectItem(position: Int){
            verPlanList.setItemChecked(position, false)
            selectedViews[position] = false
        }
        
        fun removeSelections(){
            selectedViews.clear()
        }
        
        fun isItemChecked(position: Int) = selectedViews[position] ?: false
        
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val vh = convertView ?: layoutInflater.inflate(R.layout.card_vertretungsplan, parent, false)
            
            val plan = getItem(position)
            
            vh.updateTimeTV.text = plan.updateTime.toDate().toDateTimeString()
            vh.targetDayTV.text = resources.getString(R.string.targetDay, plan.targetDay.toDate().toWeekdayDateString())
            vh.gradeTV.text = plan.grade
            
            val previewRows = arrayOf(vh.r0, vh.r1, vh.r2, vh.r3, vh.r4)
            val gPlan = plan.generalPlan.plan
            
            previewRows.forEachIndexed { index, row ->
                if (index < gPlan.size){
                    val verRow = gPlan[index]
                    with(row) {
                        lessonTV.text = verRow.lesson.toString()
                        teacherTV.text = verRow.teacher
                        verTeacherTV.text = verRow.verTeacher
                        roomTV.text = verRow.room
                        verRoomTV.text = verRow.verRoom
                        verTextTV.text = verRow.verText
                        visibility = View.VISIBLE
                    }
                }else{
                    row.visibility = View.GONE
                }
            }
            if (gPlan.size > previewRows.size){
                val overBy = gPlan.size - previewRows.size
                vh.overflowTV.text = resources.getQuantityString(R.plurals.listOverflow, overBy, overBy)
                vh.overflowTV.visibility = View.VISIBLE
            }else{
                vh.overflowTV.visibility = View.GONE
            }
    
            val transitionName = plan.fetchedTime.toString()
            //must get the layout for animation by this call otherwise it bugs out
            val previewList = vh.findViewById<LinearLayout>(R.id.previewList)
            previewList.transitionName = transitionName
            
            vh.verPlanCard.setOnLongClickListener {
                itemInCABPressed(position)
                true
            }
            vh.verPlanCard.setNoDoubleClickListener {
                if (currentlyCABActive){
                    itemInCABPressed(position)
                }else{
                    startActivity(Intent(this@ListActivity, DisplayPlanActivity::class.java).apply {
                        putExtra(DisplayPlanActivity.EXTRA_PLAN_FETCHED_TIME, plan.fetchedTime)
                    }, ActivityOptions.makeSceneTransitionAnimation(this@ListActivity,
                            previewList, transitionName)
                            .toBundle())
                }
            }
            
            vh.verPlanCard.setCardBackgroundColor(
                    if (isItemChecked(position))context.getAttribute(R.attr.highlightSelect, true).data
                    else context.getAttribute(R.attr.internalCardBackgroundColor, true).data)
            
            return vh
        }
    
        override fun getItem(position: Int) = plans[position]
    
        override fun getItemId(p0: Int): Long = p0.toLong()
    
        override fun getCount(): Int = plans.size
    }
}
