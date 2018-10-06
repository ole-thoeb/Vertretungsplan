package com.example.eloem.vertretungsplan

import com.example.eloem.vertretungsplan.util.normaliseDateString
import com.example.eloem.vertretungsplan.util.normaliseTimeString
import com.example.eloem.vertretungsplan.util.toStringWithJustTime
import com.example.eloem.vertretungsplan.util.toStringWithTime
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun date(){
        assertEquals("04.05.2018", normaliseDateString("4.5.2018"))
    }
    @Test
    fun time(){
        assertEquals("07:04", normaliseTimeString("7:4"))
    }
    @Test
    fun p(){
        val f = SimpleDateFormat("DD.MM.YYYY")
        println(f.parse("04.05.2018").time)
        val s = SimpleDateFormat("HH:mm")
        println(s.parse("07:41").time)
    }
    @Test
    fun dateToString(){
        val d = "14:07"
        val sdf = SimpleDateFormat("HH:mm")
        println(sdf.parse(d).toStringWithJustTime())
    }
}
