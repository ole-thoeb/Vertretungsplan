package com.example.eloem.vertretungsplan.ui.currentplan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.ui.editlesson.CurrentPlanViewModel
import com.example.eloem.vertretungsplan.util.*
import com.example.eloem.vertretungsplan.widget.VerPlanWidgetProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.dialog_meta_data.view.*
import kotlinx.android.synthetic.main.fragment_current_plan.container
import kotlinx.android.synthetic.main.fragment_current_plan.swiperefresh
import kotlinx.android.synthetic.main.fragment_current_plan.tabs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CurrentPlanFragment: ChildFragment(), PlanResponseHolder {
    private val args: CurrentPlanFragmentArgs by navArgs()
    private val currentPlanViewModel: CurrentPlanViewModel by viewModels()
    
    private val pages = PlanPair(MyPlanFragment(), GeneralPlanFragment())
    
    private val _generalPlan = MutableLiveData<Result<Vertretungsplan.Plan, ResponseStatus>>()
    private val _customPlan = MutableLiveData<Result<Vertretungsplan.Plan, ResponseStatus>>()
    
    override val customPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
        get() = _customPlan
    
    override val generalPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
        get() = _generalPlan
    
    private var curPlan: Vertretungsplan? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return layoutInflater.inflate(R.layout.fragment_current_plan, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        configureSupportActionBar {
            title = resources.getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
        withHost {
            hideFab()
        }
    
        container.adapter = PlanPager(pages)
        TabLayoutMediator(tabs, container) { tab, position ->
            tab.text = when(position) {
                0 -> resources.getString(R.string.tab_text_myPlan)
                1 -> resources.getString(R.string.tab_text_Plan)
                else -> throw IllegalArgumentException("unknown position $position")
            }
        }.attach()
    
        if (!currentPlanViewModel.applyedAppwidgetArgs &&
                args.calledFromAppwidget != VerPlanWidgetProvider.INVALID_APPWIDGET_ID) {
    
            currentPlanViewModel.applyedAppwidgetArgs = true
            container.currentItem = if (widgetPreferences(args.calledFromAppwidget) { isMyPlan }) 0 else 1
        }
        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            refresh(true)
        }
        refresh(false)
        
        globalViewModel.currentLocalPlan(currentGrade).observe(viewLifecycleOwner) {
            curPlan = it
        }
    }
    
    private fun refresh(force: Boolean) {
        Log.d(TAG, "refreshing current plan")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (!force) { //if forced the swiperefresh is already spinning
                _customPlan.postValue(Result.Failure(ResponseStatus.REFRESHING))
                _generalPlan.postValue(Result.Failure(ResponseStatus.REFRESHING))
            }
            val result = globalViewModel.currentPlan(force)
            //Log.d(TAG, "done refreshing got result: $result")
            _customPlan.postValue(result.withSuccess { it.customPlan }.withFailure { it.asResponseStaus() })
            _generalPlan.postValue(result.withSuccess { it.generalPlan }.withFailure { it.asResponseStaus() })
            swiperefresh.isRefreshing = false
            configureSupportActionBar {
                title = when (result) {
                    is Result.Success -> resources.getString(
                            R.string.actionbar_time_info,
                            result.value.updateTime.toDate().toTimeString(),
                            result.value.fetchedTime.toDate().toTimeString()
                    )
                    is Result.Failure -> resources.getString(R.string.app_name)
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_actions_current_plan, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(R.id.action_global_settingsFragment)
                true
            }
            R.id.listVerplan -> {
                true
            }
            R.id.timetable -> {
                findNavController()
                        .navigate(R.id.action_currentPlanFragment_to_timetableOverviewFragment)
                true
            }
            R.id.metaData -> {
                curPlan?.let {
                    showMetaData(it)
                }
                true
            }
            R.id.deleteTimetable -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    @SuppressLint("InflateParams")
    fun showMetaData(verPlan: Vertretungsplan){
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
    
    private inner class PlanPager(private val plans: PlanPair) : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 2
    
        override fun createFragment(position: Int): Fragment = when(position) {
            0 -> plans.cPlan
            1 -> plans.gPlan
            else -> throw IllegalArgumentException("unknown position $position")
        }
    }
    
    private data class PlanPair(val cPlan: PlanFragment, val gPlan: PlanFragment)
    
    companion object {
        private const val TAG = "CurrentPlanFragment"
    }
}