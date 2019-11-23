package com.example.eloem.vertretungsplan.ui.planlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.eloem.vertretungsplan.database.PlanRepository
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.ContextOwner
import com.example.eloem.vertretungsplan.util.planListPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log10

class PlanListViewModel(application: Application): AndroidViewModel(application), ContextOwner {
    
    private val TAG = "PlanListViewModel"
    private val repository: PlanRepository = PlanRepository.create(getApplication())
    override val ctx: Context get() = getApplication()
    
    private val _plans: MediatorLiveData<List<Vertretungsplan>> = MediatorLiveData()
    
    val plans: LiveData<List<Vertretungsplan>> get() = _plans
    
    private var cSource: LiveData<List<Vertretungsplan>>? = null
    fun refreshPlans() = viewModelScope.launch(Dispatchers.IO) {
        planListPreferences {
            val enabledGrades = mutableListOf<Vertretungsplan.Grade>()
            if (!isEfEnabled && !isQ1Enabled && !isQ2Enabled) enabledGrades.addAll(Vertretungsplan.Grade.values())
            else{
                if (isEfEnabled) enabledGrades.add(Vertretungsplan.Grade.EF)
                if (isQ1Enabled) enabledGrades.add(Vertretungsplan.Grade.Q1)
                if (isQ2Enabled) enabledGrades.add(Vertretungsplan.Grade.Q2)
            }
    
            val latestTime = if (seekerProgress == 100) 0
            else System.currentTimeMillis() - progressToDays(seekerProgress) * 1000 * 60 * 60 * 24
    
//        val filteredPlans = getAllVerPlans(this).filter {
//            (!forMe || it.customPlan.plan.isNotEmpty()) &&
//                    it.grade in enabledGrades && it.updateTime > latestTime
//        }
//        if (lastOfDay) {
//            val groupedPlans = filteredPlans.groupBy {
//                it.grade.toIntCharByChar().toLong() + it.targetDay
//            }.values.toList()
//            return List(groupedPlans.size) {
//                groupedPlans[it].maxBy { plan ->
//                    plan.fetchedTime
//                }!!
//            }
//        }
            val source = repository.getVerPlans(enabledGrades, latestTime, isFilterForMeActive, isFilterLastOfDayActive)
            withContext(Dispatchers.Main) {
                cSource?.let { _plans.removeSource(it) }
                _plans.addSource(source) { _plans.value = it }
            }
            cSource = source
        }
    }
    
    fun progressToDays(progress: Int) = if (progress == 0) 1
    else 2 + (0.3 * progress * log10(progress.toFloat())).toInt()
    
    fun deletePlans(plans: List<Vertretungsplan>) = viewModelScope.launch(Dispatchers.IO) {
        plans.forEach {
            repository.deleteVerPlan(it)
        }
    }
}