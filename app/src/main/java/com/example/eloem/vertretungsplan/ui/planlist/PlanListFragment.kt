package com.example.eloem.vertretungsplan.ui.planlist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.card_vertretungsplan.view.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.plan_row.view.*
import org.jetbrains.anko.attr
import kotlin.math.log10

class PlanListFragment : ChildFragment() {
    
    private var currentlyCABActive = false
    
    private val listViewModel: PlanListViewModel by viewModels()
    
    private lateinit var mAdapter: ListAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_plan_list, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        configureSupportActionBar {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.title_plan_list)
        }
    
        withHost {
            hideFab()
        }
        
        mAdapter = ListAdapter(emptyList(), ctx)
        var numberOfSelectedViews = 0
        with(verPlanList) {
            adapter = mAdapter
            //choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
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
                            listViewModel.deletePlans(mAdapter.selectedPlans)
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
    
                    requireActivity().window.setStatusBarAndIconColors(ctx.attr(R.attr.colorAccent).data)
                    return true
                }
            
                override fun onDestroyActionMode(mode: ActionMode) {
                    // Here you can make any necessary updates to the activity when
                    // the CAB is removed. By default, selected items are deselected/unchecked.
                    numberOfSelectedViews = 0
                    currentlyCABActive = false
                    
                    requireActivity().window.setStatusBarAndIconColors(ctx.attr(R.attr.toolbarColor).data)
                
                    mAdapter.removeSelections()
                }
            
                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    // Here you can perform updates to the CAB due to
                    // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                    return false
                }
            })
        }
        
        listViewModel.plans.observe(viewLifecycleOwner) {
            mAdapter.plans = it
            mAdapter.notifyDataSetChanged()
        }
        listViewModel.refreshPlans()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_action_list, menu)
    }
    
    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        R.id.listFilter -> {
            planListPreferences {
                val custView = layoutInflater.inflate(R.layout.dialog_filter, null).apply {
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            val days = listViewModel.progressToDays(progress)
                            periodTV.text = if (progress == 100) resources.getString(R.string.periodDefault)
                            else resources.getQuantityString(R.plurals.periodText, days, days)
                        }
            
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }
            
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })
                    seekBar.progress = seekerProgress
                    EFCB.isChecked = isEfEnabled
                    Q1CB.isChecked = isQ1Enabled
                    Q2CB.isChecked = isQ2Enabled
                    forMeCB.isChecked = isFilterForMeActive
                    lastOfDayCB.isChecked = isFilterLastOfDayActive
                }
                MaterialAlertDialogBuilder(ctx)
                        .setView(custView)
                        .setTitle(R.string.dialog_filterTitle)
                        .setPositiveButton(R.string.dialog_filterPositive) { _, _->
                            with(custView) {
                                seekerProgress = seekBar.progress
                                isEfEnabled = EFCB.isChecked
                                isQ1Enabled = Q1CB.isChecked
                                isQ2Enabled = Q2CB.isChecked
                                isFilterForMeActive = forMeCB.isChecked
                                isFilterLastOfDayActive = lastOfDayCB.isChecked
                            }
                            listViewModel.refreshPlans()
                        }
                        /*.setNeutralButton(R.string.cancel) { dialog, which ->
                        
                        }*/
                        .setNegativeButton(R.string.dialog_filterNegative) { _, _ ->
                            seekerProgress = 100
                            isEfEnabled = true
                            isQ1Enabled = true
                            isQ2Enabled = true
                            isFilterForMeActive = false
                            isFilterLastOfDayActive = false
                            listViewModel.refreshPlans()
                        }
                        .show()
                true
            }
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
            vh.gradeTV.text = plan.grade.toString()
            
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
                    findNavController().navigate(
                            PlanListFragmentDirections.actionPlanListFragmentToDisplayPlanFragment(plan.id, true)
                    )
                }
            }

            vh.verPlanCard.setCardBackgroundColor(ctx.attr(if (isItemChecked(position)) R.attr.colorAccent else R.attr.internalCardBackgroundColor).data)

            return vh
        }
        
        override fun getItem(position: Int) = plans[position]
        
        override fun getItemId(p0: Int): Long = p0.toLong()
        
        override fun getCount(): Int = plans.size
    }
}