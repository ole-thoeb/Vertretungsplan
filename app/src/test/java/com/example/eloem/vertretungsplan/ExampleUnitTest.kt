package com.example.eloem.vertretungsplan

import com.example.eloem.vertretungsplan.util.*
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
        println(sdf.parse(d).toTimeString())
    }
    @Test
    fun weekdayMatch(){
        val str = """<B>Klasse / Erweitert  13.2. / Mittwoch</B>"""
        val match = """(?<=/\s)\w+""".toRegex().find(str)
        println(match!!.next()!!.groups[0]!!.value)
    }
    @Test
    fun tableTest(){
        val html = """
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"><meta http-equiv="expires" content="0"><meta name="keywords" content="Stundenplan, timetable">
<meta name="GENERATOR" content="Untis 2019">
<title>Untis 2019  Schuljahr 2018/19  EUROPASCHULE BORNHEIM  2</title>
<style type="text/css">
a {color:#000000;}
</style>
</head>
<body bgcolor="#FFFFFF">
<CENTER><font size="3" face="Arial"  color="#000000">
<TABLE border="0" cellpadding="1" ><TR><TD rowspan="2" width="5"></TD><TD><b>EUROPASCHULE BORNHEIM</b></TD><TD rowspan="2" width="5"></TD><TD>Schuljahr 2018/19</TD><TD rowspan="2" width="5"></TD><TD align="right"> <b>Untis 2019</b></TD><TD rowspan="2" width="5"></TD></TR><TR><TD>D-53332, GOETHESTR. 1</TD><TD></TD><TD align="right">8.2.2019 7:29</TD></TR></TABLE><BR><font size="5" face="Arial">
<B>Klasse / Erweitert  8.2. / Freitag</B>
</font>
<BR>
<font size="6" face="Arial">
EF
</font>
<BR>
<TABLE border="3" rules="all" bgcolor="#E7E7E7" cellpadding="1" cellspacing="1">
<TR>
<TD align=center><font size="3" face="Arial">
Stunde
</font> </TD>
<TD align=center><font size="3" face="Arial">
(Lehrer)
</font> </TD>
<TD align=center><font size="3" face="Arial">
Vertreter
</font> </TD>
<TD align=center><font size="3" face="Arial">
Klasse(n)
</font> </TD>
<TD align=center><font size="3" face="Arial">
Raum
</font> </TD>
<TD align=center><font size="3" face="Arial">
Fach
</font> </TD>
<TD align=center><font size="3" face="Arial">
(Raum)
</font> </TD>
<TD align=center><font size="3" face="Arial">
Vertretungs-Text
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
2
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
NEID
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
195
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
D-GK1
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
195
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
3
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
FORN
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
127
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
PH-GK1
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
127
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
3
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
MICH
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
SP2
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
SP-GK6
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
SP2
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
4
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
HART
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
190
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
4
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
ROES
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
192
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
WOLF
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
195
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
4
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
BOER
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
193
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">
MICH
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
SP2
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
4
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
KOB
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
195
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial" color="#010101">
4
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
GEIS
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
+
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
---
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">
323
</font> </TD>
<TD align=center><font size="3" face="Arial" color="#010101">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
SZA
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
194
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
RING
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
188
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">
GATZ
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
157
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">
FORN
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
127
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
BÜR
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
193
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
ROES
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
192
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
CSAP
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
190
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
<TD align=center><font size="3" face="Arial">
Zeugnisausgabe
</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
HART
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
193
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
MASH
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
257
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
ROTH
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
SP1
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
VOI
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
195
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
GEIS
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
123
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
FRIE
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
194
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
5
</font> </TD>
<TD align=center><font size="3" face="Arial">
CRON
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
021
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
DIOS
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
194
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
COMA
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
195
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
BÜR
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
193
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
REH
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
188
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
ROES
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
192
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
6
</font> </TD>
<TD align=center><font size="3" face="Arial">
HEIZ
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
190
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
BRÜN
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
SP4
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
SCHÄ
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
190
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
KITT
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
147
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
DIEF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
193
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
NEID
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
195
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
BING
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
256
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR><TR>
<TD align=center><font size="3" face="Arial">
8 - 9
</font> </TD>
<TD align=center><font size="3" face="Arial">
ESCH
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
EF
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
---
</font> </TD>
<TD align=center><font size="3" face="Arial">
192
</font> </TD>
<TD align=center><font size="3" face="Arial">

</font> </TD>
</TR></TABLE><TABLE cellspacing="1" cellpadding="1"><TR><TD valign=bottom> <font  size="4" face="Arial" color="#0000FF">Untis </font></TD><TD valign=bottom> <font  size="2" face="Arial" color="#0000FF"> 2019 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font></TD><TD valign=bottom><A HREF="Ver_Kla_A.htm"><img src="GpIndex.gif" width="48" height="48" border="0" alt="Verzweigung zu Index"></A></TD><TD valign=bottom><A HREF="Ver_Kla_A_Q1.htm"><img src="GpNext.gif" width="40" height="28" border="0" alt="Nächster Stundenplan"></A></TD><TD valign=bottom> <font  size="4" face="Arial" color="#0000FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<A HREF="http://www.untis.at" target="_blank"title="Gruber &amp; Petters Homepage">Untis</A> <A HREF="http://www.school-timetabling.com" target="_blank">Stundenplansoftware</A></font></TD></TR></TABLE><font size="3" face="Arial"  color="#000000">
Periode10   8.2.2019
</font></CENTER>
</body>
</html>

"""
        println(extractTable(html))
    }
}
