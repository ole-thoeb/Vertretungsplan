package com.example.eloem.vertretungsplan

import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.example.eloem.vertretungsplan.util.intPref

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun prefTest() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        with(appContext){
            var testInt by intPref("testInt")
            testInt = 33
        }
        val testInt2 by appContext.intPref("testInt")
        assertEquals(33, testInt2)
    }
    
    @Test
    fun colorParse(){
        assertEquals(Color.parseColor("#FFFAFAFA"), 0xFFFAFAFA.toInt())
    }
}
