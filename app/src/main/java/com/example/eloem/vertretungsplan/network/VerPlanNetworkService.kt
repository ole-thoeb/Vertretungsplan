package com.example.eloem.vertretungsplan.network

import android.content.Context
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.Result

interface VerPlanNetworkService {
    suspend fun planForGrade(grade: Vertretungsplan.Grade): Result<ResponseModel.VerPlan, ResponseModel.Error>
}

fun VerPlanNetworkService(context: Context): VerPlanNetworkService = VolleyVerPlanService(context)