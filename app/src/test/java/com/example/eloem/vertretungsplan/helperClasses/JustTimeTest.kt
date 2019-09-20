package com.example.eloem.vertretungsplan.helperClasses

import org.junit.Test

import org.junit.Assert.*

class JustTimeTest {

    @Test
    fun compareTo() {
        val smaller = JustTime(15, 56)
        val bigger = JustTime(16, 1)
        assertEquals(true, smaller < bigger)
        assertEquals(false, smaller > bigger)
        assertEquals(false, bigger < smaller)
    }
}