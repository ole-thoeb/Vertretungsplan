package com.example.eloem.vertretungsplan.ui.currentplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.util.Result

interface PlanResponseHolder {
    val customPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
    val generalPlan: LiveData<Result<Vertretungsplan.Plan, ResponseStatus>>
}

enum class ResponseStatus { NO_INTERNET, PARSE_ERROR, REFRESHING }

fun ResponseModel.Error.asResponseStaus(): ResponseStatus = when (this) {
    ResponseModel.Error.NO_INTERNET -> ResponseStatus.NO_INTERNET
    ResponseModel.Error.PARSE_ERROR -> ResponseStatus.PARSE_ERROR
}