package com.example.eloem.vertretungsplan.ui.currentplan

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.ui.ChildFragment
import com.example.eloem.vertretungsplan.util.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_current_plan.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplayPlanFragment: ChildFragment(), PlanResponseHolder {
    
    private val args: DisplayPlanFragmentArgs by navArgs()
    private val displayPlanViewModel: DisplayPlanViewModel by viewModels()
    
    private val _generalPlan = MutableLiveData<Result<Vertretungsplan.Plan, ResponseStatus>>()
    private val _customPlan = MutableLiveData<Result<Vertretungsplan.Plan, ResponseStatus>>()
    
    override val customPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
        get() = _customPlan
    
    override val generalPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
        get() = _generalPlan
    
    lateinit var verPlan: Vertretungsplan
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_current_plan, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        configureSupportActionBar {
            title = resources.getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }
        withHost {
            hideFab()
        }
    
        configurViewPager(tabs, container)
        
        swiperefresh.isEnabled = false
    
        _customPlan.postValue(Result.Failure(ResponseStatus.REFRESHING))
        _generalPlan.postValue(Result.Failure(ResponseStatus.REFRESHING))
        
        lifecycleScope.launch(Dispatchers.IO) {
            val plan = globalViewModel.getVerPlan(args.verPlanId)
            if (plan != null) {
                verPlan = plan
                _generalPlan.postValue(Result.Success(plan.generalPlan))
                _customPlan.postValue(Result.Success(plan.customPlan))
                
                withContext(Dispatchers.Main) {
                    configureSupportActionBar {
                        title = plan.targetDay.toDate().toWeekdayDateString()
                    }
                    if (!displayPlanViewModel.appliedStartingScreenSwitch) {
                        container.currentItem = if (args.shouldStartWithGeneral) 1 else 0
                        displayPlanViewModel.appliedStartingScreenSwitch = true
                    }
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_actions_display_plan, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.metaData -> {
            showMetaData(verPlan)
            true
        }
        R.id.relatedTimetable -> {
            if (verPlan.computedWith == -1L) {
                Snackbar.make(hostActivity.rootView, R.string.noRelatedTimetable, Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                findNavController().navigate(DisplayPlanFragmentDirections
                        .actionDisplayPlanFragmentToTimetableFragment(verPlan.computedWith, false)
                )
            }
            true
        }
        else -> false
    }
}