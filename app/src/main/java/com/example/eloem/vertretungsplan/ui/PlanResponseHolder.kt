package com.example.eloem.vertretungsplan.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.util.Result

interface PlanResponseHolder {
    val customPlan: LiveData<Result<Vertretungsplan.Plan, ResponseModel.Error>>
    val generalPlan: LiveData<Result<Vertretungsplan.Plan, ResponseModel.Error>>
}