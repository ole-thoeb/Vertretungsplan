package com.example.eloem.vertretungsplan.ui.currentplan

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.*
import com.example.eloem.vertretungsplan.widget.VerPlanWidgetProvider
import kotlinx.android.synthetic.main.fragment_current_plan.container
import kotlinx.android.synthetic.main.fragment_current_plan.swiperefresh
import kotlinx.android.synthetic.main.fragment_current_plan.tabs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentPlanFragment: ChildFragment(), PlanResponseHolder {
    private val args: CurrentPlanFragmentArgs by navArgs()
    private val currentPlanViewModel: CurrentPlanViewModel by viewModels()
    
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
    
        configurViewPager(tabs, container)
    
        if (!currentPlanViewModel.appliedAppwidgetArgs &&
                args.calledFromAppwidget != VerPlanWidgetProvider.INVALID_APPWIDGET_ID) {
    
            currentPlanViewModel.appliedAppwidgetArgs = true
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
                findNavController().navigate(R.id.action_currentPlanFragment_to_planListFragment)
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
//            R.id.deleteTimetable -> {
//                true
//            }
            R.id.refresh -> {
                swiperefresh.isRefreshing = true
                refresh(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    companion object {
        private const val TAG = "CurrentPlanFragment"
    }
}