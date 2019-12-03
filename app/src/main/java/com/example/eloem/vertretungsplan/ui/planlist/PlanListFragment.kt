package com.example.eloem.vertretungsplan.ui.planlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.recyclerView.ContextAdapter
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.card_vertretungsplan.view.*
import kotlinx.android.synthetic.main.dialog_filter.*
import kotlinx.android.synthetic.main.fragment_plan_list.*
import kotlinx.android.synthetic.main.plan_row.view.*
import org.jetbrains.anko.attr

class PlanListFragment : ChildFragment() {
    private val listViewModel: PlanListViewModel by viewModels()
    
    private lateinit var adapter: VerplanAdapter
    private var actionMode: ActionMode? = null
    
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            // Respond to clicks on the actions in the CAB
            return when (item.itemId) {
                R.id.deletePlan -> {
                    listViewModel.deletePlans(adapter.selectedPlans)
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
            listViewModel.isCABActive = true
        
            withHost {
                window.setStatusBarAndIconColors(differentShade(ctx.attr(R.attr.colorSecondaryVariant).data, -0.15f))
            }
            return true
        }
    
        override fun onDestroyActionMode(mode: ActionMode) {
            // Here you can make any necessary updates to the activity when
            // the CAB is removed. By default, selected items are deselected/unchecked.
            listViewModel.numberOfSelectedViews = 0
            listViewModel.isCABActive = false
            actionMode = null
        
            requireActivity().window.setStatusBarAndIconColors(ctx.attr(R.attr.statusBarColor).data)
        
            adapter.removeSelections()
        }
    
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false
    }
    
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
        
        adapter = VerplanAdapter()
        verPlanList.adapter = adapter
        verPlanList.layoutManager = LinearLayoutManager(ctx)
        
        if (listViewModel.isCABActive) {
            actionMode = hostActivity.startSupportActionMode(actionModeCallback)
            setActionModeTitle()
        }
        progressBar.visibility = ProgressBar.VISIBLE
        listViewModel.plans.observe(viewLifecycleOwner) {
            progressBar.visibility = ProgressBar.GONE
            adapter.plans = it
            adapter.notifyDataSetChanged()
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
    
    private fun setActionModeTitle() {
        actionMode?.title = resources.getString(R.string.titleCAB, listViewModel.numberOfSelectedViews)
    }
    
    inner class VerplanAdapter(var plans: List<Vertretungsplan> = emptyList()) : ContextAdapter<VerplanAdapter.PlanCard>() {
        inner class PlanCard(layout: View) : RecyclerView.ViewHolder(layout) {
            val verPlanCard: CardView = layout.findViewById(R.id.verPlanCard)
            val updateTimeTV: TextView = layout.findViewById(R.id.updateTimeTV)
            val targetDayTV: TextView = layout.findViewById(R.id.targetDayTV)
            val gradeTV: TextView = layout.findViewById(R.id.gradeTV)
            val previewList: LinearLayout = layout.findViewById(R.id.previewList)
            val previewRows = arrayOf(layout.r0, layout.r1, layout.r2, layout.r3, layout.r4)
            val overflowTV: TextView = layout.findViewById(R.id.overflowTV)
        }
    
    
        private val selectedCardBackground: Int by lazy {
            val secondaryVariant = ctx.attr(R.attr.colorSecondary).data
            if (isInDarkMode) {
                val surfaceColor = ctx.attr(R.attr.colorSurface).data
                ArgbEvaluator.getInstance().evaluate(0.08f, surfaceColor, secondaryVariant) as Int
            } else {
                secondaryVariant
            }
        }
        
        val selectedPlans get() = plans.subList(listViewModel.selectedViews.mapNotNull { if (it.value) it.key else null })
    
        private fun itemInCABPressed(position: Int) {
            if (actionMode == null) {
                actionMode = hostActivity.startSupportActionMode(actionModeCallback)
            }
            val newState = !isItemSelected(position)
            isItemSelected(position, newState)
    
            if (newState) {
                listViewModel.numberOfSelectedViews++
            } else {
                listViewModel.numberOfSelectedViews--
                if (listViewModel.numberOfSelectedViews == 0) {
                    actionMode?.finish()
                }
            }
            setActionModeTitle()
            notifyItemChanged(position)
        }
    
        fun removeSelections(){
            listViewModel.selectedViews.keys.forEach {
                notifyItemChanged(it)
            }
            listViewModel.selectedViews.clear()
        }
    
        private fun isItemSelected(position: Int) = listViewModel.selectedViews[position] ?: false
        private fun isItemSelected(position: Int, value: Boolean) {
            listViewModel.selectedViews[position] = value
        }
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanCard {
            return PlanCard(inflate(R.layout.card_vertretungsplan, parent))
        }
    
        override fun getItemCount(): Int = plans.size
    
        override fun onBindViewHolder(holder: PlanCard, position: Int) {
            val plan = plans[position]
    
            holder.updateTimeTV.text = plan.updateTime.toDate().toDateTimeString()
            holder.targetDayTV.text = ctx.getString(R.string.targetDay, plan.targetDay.toDate().toWeekdayDateString())
            holder.gradeTV.text = plan.grade.toString()
    
            val gPlan = plan.generalPlan.plan
    
            holder.previewRows.forEachIndexed { index, row ->
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
            if (gPlan.size > holder.previewRows.size){
                val overBy = gPlan.size - holder.previewRows.size
                holder.overflowTV.text = ctx.resources.getQuantityString(R.plurals.listOverflow, overBy, overBy)
                holder.overflowTV.visibility = View.VISIBLE
            }else{
                holder.overflowTV.visibility = View.GONE
            }
    
            val transitionName = plan.fetchedTime.toString()
            //must get the layout for animation by this call otherwise it bugs out
            holder.previewList.transitionName = transitionName
    
            holder.verPlanCard.apply {
                setOnLongClickListener {
                    itemInCABPressed(holder.adapterPosition)
                    true
                }
                setNoDoubleClickListener {
                    if (listViewModel.isCABActive) {
                        itemInCABPressed(holder.adapterPosition)
                    } else {
                        findNavController().navigate(
                                PlanListFragmentDirections.actionPlanListFragmentToDisplayPlanFragment(plans[holder.adapterPosition].id, true)
                        )
                    }
                }
    
                setCardBackgroundColor(if (isItemSelected(position)) selectedCardBackground else ctx.attr(R.attr.internalCardBackgroundColor).data)
            }
        }
    }
}