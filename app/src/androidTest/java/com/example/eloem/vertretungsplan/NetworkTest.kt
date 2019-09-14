package com.example.eloem.vertretungsplan

import android.content.Context
import org.junit.Test
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnitRunner
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.VolleyVerPlanService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After

class NetworkTest {
    
    @Test
    fun volleyNetwork() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val client = VolleyVerPlanService(ctx)
        println(runBlocking {
            client.planForGrade(Vertretungsplan.Grade.Q1)
        })
    }
    
    @After
    fun teardown() {
    
    }
}