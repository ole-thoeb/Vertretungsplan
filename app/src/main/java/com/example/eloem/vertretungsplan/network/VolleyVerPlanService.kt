package com.example.eloem.vertretungsplan.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class VolleyVerPlanService(context: Context) : VerPlanNetworkService {
    val queue: RequestQueue = Volley.newRequestQueue(context)
    
    suspend fun simpleRequest(url: String): Result<String, VolleyError> = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "fetching from $url")
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> {
                    continuation.resume(Result.Success(it)) { error ->  Log.e(TAG, "canceled in Success", error) }
                }, Response.ErrorListener {
            continuation.resume(Result.Failure(it)) { error ->  Log.e(TAG, "canceled in error", error) }
        }
        )
        
        continuation.invokeOnCancellation { stringRequest.cancel() }
        
        queue.add(stringRequest)
    }
    
    override suspend fun planForGrade(grade: Vertretungsplan.Grade): Result<ResponseModel.VerPlan, ResponseModel.Error> {
        
        /*if (readDownloadAllPlans(ctx)) {
            val otherGrades = Vertretungsplan.Grade.values().toMutableList()
            otherGrades.remove(grade)
            withContext(Dispatchers.IO) {
                otherGrades.forEach { grade ->
                    launch {
                        when (val response = simpleRequest(grade.url)) {
                            is Result.Success -> when (val planResult = ResponseModel.VerPlan.fromString(response.value)) {
                                is Result.Success -> {
                                
                                }
                                is Result.Failure -> {
                                
                                }
                            }
                            is Result.Failure -> {
                                Log.e(TAG, response.error.localizedMessage)
                            }
                        }.exhaustive()
                    }
                }
            }
        }*/
        return withContext(Dispatchers.IO) {
            val response = simpleRequest(grade.url)
            response.withFailure { ResponseModel.Error.NO_INTERNET }
                    .chainSuccess { ResponseModel.VerPlan.fromString(it) }
        }
    }
    
    companion object {
        private const val TAG = "VolleyNetworkService"
    }
}