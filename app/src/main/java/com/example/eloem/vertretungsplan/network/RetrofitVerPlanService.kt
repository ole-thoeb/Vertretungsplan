package com.example.eloem.vertretungsplan.network

import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.Result
import retrofit2.Retrofit
import retrofit2.http.GET

interface RetrofitVerPlanService {
    
    @GET("Ver_Kla_A_EF.htm")
    suspend fun efPlan(): ResponseModel.VerPlan
    
    @GET("Ver_Kla_A_Q1.htm")
    suspend fun q1Plan(): ResponseModel.VerPlan
    
    @GET("Ver_Kla_A_Q2.htm")
    suspend fun q2Plan(): ResponseModel.VerPlan
    
    companion object {
        fun create(): RetrofitVerPlanService {
        
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://www.europaschule-bornheim.eu/fileadmin/vertretung/")
                    .addConverterFactory(VerPlanConverterFactory.create())
                    .build()
        
            return retrofit.create(RetrofitVerPlanService::class.java)
        }
    }
}
//
//suspend fun RetrofitVerPlanService.planForGrade(
//        grade: Vertretungsplan.Grade
//): Result<ResponseModel.VerPlan, ResponseModel.Error> = tryCatchResult({
//    when (grade) {
//        Vertretungsplan.Grade.EF -> efPlan()
//        Vertretungsplan.Grade.Q1 -> q1Plan()
//        Vertretungsplan.Grade.Q2 -> q2Plan()
//    }
//}, { ResponseModel.Error.NO_INTERNET })

suspend fun RetrofitVerPlanService.planForGrade(
        grade: Vertretungsplan.Grade
): Result<ResponseModel.VerPlan, ResponseModel.Error> =
    Result.Success(when (grade) {
        Vertretungsplan.Grade.EF -> efPlan()
        Vertretungsplan.Grade.Q1 -> q1Plan()
        Vertretungsplan.Grade.Q2 -> q2Plan()
    })
