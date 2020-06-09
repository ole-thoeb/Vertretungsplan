package com.example.eloem.vertretungsplan

import android.content.Context
import org.junit.Test
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnitRunner
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.VolleyVerPlanService
import com.example.eloem.vertretungsplan.util.throwError
import com.example.eloem.vertretungsplan.util.toDate
import com.example.eloem.vertretungsplan.util.withSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After

class NetworkTest {
    
    @Test
    fun volleyNetwork() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val client = VolleyVerPlanService(ctx)
        runBlocking {
            val response = client.planForGrade(Vertretungsplan.Grade.EF).throwError()
            println(response)
            val plan = response.toVertretungsplan(ctx, null)
            println(plan)
            plan.targetDay.toDate()
        }
    }
    
    @After
    fun teardown() {
    
    }
}