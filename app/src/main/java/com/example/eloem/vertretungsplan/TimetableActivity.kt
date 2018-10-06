package com.example.eloem.vertretungsplan

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.util.isDarkColor
import com.example.eloem.vertretungsplan.util.readTimetable
import com.example.eloem.vertretungsplan.util.writeTimetable
import kotlinx.android.synthetic.main.activity_timetable.*
import kotlinx.android.synthetic.main.timetable_row.view.*

class TimetableActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        fillTable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.title_timetable)
    }
    
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_actions_timetable, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId){
        R.id.settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.deleteTimetable -> {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_deleteTimetable_message)
                    .setPositiveButton(R.string.ok){ _, _ ->
                        writeTimetable(Timetable(4, 10), this)
                        //refresh activity
                        finish()
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.cancel){ _, _ ->
                        //do nothing
                    }.show()
            true
        }
        R.id.home -> {
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
    /*private fun fillTable(){
        val dp5 = dpToPx(5f, this)//sehr recourcen aufwändig
        val dp45 = dpToPx(45f, this)
        val timetable = readTimetable(this)
        for(i in 0..10){
            //neue Zeile
            val tr = TableRow(this)
            tr.layoutParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tr.isBaselineAligned = false //damit multiline textview nicht nach unten geschoben ist
            
            //Stunde
            val tvLesson = TextView(this)
            tvLesson.text = (i+1).toString()
            tvLesson.textSize = 16f
            tvLesson.gravity = Gravity.CENTER
            tvLesson.setPadding(dp5, 0, dp5, 0)
            
            tr.addView(tvLesson)
            for(j in 0..4){
                //textfeld configurieren
                val tvSubject = TextView(this)
                tvSubject.text = timetable[j][i].subject
                tvSubject.textSize = 15f
                tvSubject.setBackgroundColor(Color.parseColor(timetable[j][i].color))
                if (isDarkColor(timetable[j][i].color)) tvSubject.setTextColor(Color.WHITE)
                else tvSubject.setTextColor(Color.BLACK)
                tvSubject.gravity = Gravity.CENTER
                tvSubject.minimumHeight = dp45
                tvSubject.width = 0
                tvSubject.setOnClickListener {
                    val editIntent = Intent(this, AddLessonActivity::class.java)
                    editIntent.putExtra("Day", j)
                    editIntent.putExtra("Lesson", i)
                    startActivity(editIntent)
                }
                tr.addView(tvSubject)
            }
            table.addView(tr)
        }
    }*/
    private fun fillTable(){
        val timetable = readTimetable(this)
        
        fun configureSubjectTv(tv: TextView, day: Int, lesson: Int){
            with(tv) {
                text = timetable[day][lesson].subject
                setBackgroundColor(Color.parseColor(timetable[day][lesson].color))
                if (isDarkColor(timetable[day][lesson].color)) setTextColor(Color.WHITE)
                else setTextColor(Color.BLACK)
                setOnClickListener {
                    startActivity(Intent(this@TimetableActivity,
                            AddLessonActivity::class.java).apply {
                        putExtra("Day", day)
                        putExtra("Lesson", lesson)
                    })
                }
            }
        }
        
        for (i in 0..10){
            val tr = layoutInflater.inflate(R.layout.timetable_row, table, false)
            with(tr) {
                lessonTV.text = (i + 1).toString()
                
                configureSubjectTv(subject1TV, 0, i)
                configureSubjectTv(subject2TV, 1, i)
                configureSubjectTv(subject3TV, 2, i)
                configureSubjectTv(subject4TV, 3, i)
                configureSubjectTv(subject5TV, 4, i)
            }
            table.addView(tr)
        }
    }
}